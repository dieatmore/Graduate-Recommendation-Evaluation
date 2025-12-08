package org.example.graduaterecommendationevaluation.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.graduaterecommendationevaluation.dox.SubmitFile;
import org.example.graduaterecommendationevaluation.dox.TargetSubmit;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.SubmitFileRepository;
import org.example.graduaterecommendationevaluation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final UserService userService;
    private final CollegeService collegeService;
    private final SubmitFileRepository submitFileRepository;
    private final TargetService targetService;

    @Value("${my.upload}")
    private String rootDirectory;

    // 是否存在材料
    public SubmitFile existFile(Long fileId) {
        SubmitFile sf = submitFileRepository.findById(fileId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("该材料不存在！")
                        .build());
        // 校验本地文件是否存在（防止数据库有记录但文件被删除）
        File localFile = new File(sf.getPath());
        if (!localFile.exists() || !localFile.isFile()) {
            throw XException.builder().message("文件已被删除或移动!").build();
        }
        return sf;
    }

    // 上传材料
    public void uploadFile(Long targetSubmitId, MultipartFile file, Long uid) throws IOException {
        User user = userService.getUserById(uid);

        String collegeName = collegeService.getCollege(user.getCollegeId()).getName();
        String categoryName = collegeService.getCategory(user.getCategoryId()).getName();
        String majorName = collegeService.getMajor(user.getMajorId()).getName();

        TargetSubmit ts = targetService.getSubmitById(targetSubmitId);

        // 拼接相对路径（学院/类别/专业/姓名-账号/）
        String relativeDir = String.format("推免材料/%s/%s/%s/%s-%s/%s",
                collegeName, categoryName, majorName,
                user.getName(), user.getAccount(),ts.getName()); // 账号作为学号使用

        // 构建本地存储目录（根路径 + 相对路径），并创建目录（不存在则创建）
        Path localDir = Paths.get(rootDirectory, relativeDir);
        Files.createDirectories(localDir);

        // 处理文件名（避免空文件名）
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw XException.builder().message("上传文件名称异常").build();
        }
        // _替换非法字符
        String validFilename = originalFilename.replaceAll("[\\\\/:*?\"<>|]", "_");

        // 重名删去旧材料
        SubmitFile existingFile = submitFileRepository.findByTargetSubmitIdAndFilename(targetSubmitId, validFilename);
        if (existingFile != null) {
            submitFileRepository.delete(existingFile);
        }

        // 保存文件到本地
        Path localFilePath = localDir.resolve(validFilename);
        file.transferTo(localFilePath);
        log.debug("文件本地保存成功，路径：{}", localFilePath);

        // 构建提交文件记录（数据库存储完整相对路径）
        SubmitFile submitFile = SubmitFile.builder()
                .targetSubmitId(targetSubmitId)
                .filename(validFilename)
                .path(localFilePath.toString())
                .build();

        // 插入数据库
        submitFileRepository.save(submitFile);
    }


    // 删除材料
    public void deleteFile(Long fileId){
        SubmitFile file = submitFileRepository.findById(fileId)
                .orElseThrow(()-> XException.builder()
                        .number(Code.ERROR)
                        .message("不存在该文件！")
                        .build());
        String relativePath = file.getPath(); // 数据库存储的路径
        File physicalFile = Paths.get(relativePath).toFile();

        // 删除本地物理文件
        if (physicalFile.exists()) {
            boolean deleteSuccess = physicalFile.delete();
            if (!deleteSuccess) {
                throw XException.builder().number(Code.ERROR).message("删除文件失败！").build();
            }
        } else {
            // 物理文件不存在，但数据库有记录
            throw XException.builder().number(Code.ERROR).message("物理文件不存在！").build();
        }

        // 删除数据库记录
        submitFileRepository.deleteById(fileId);
    }

    // 打开材料
    public void openFile(SubmitFile sf, HttpServletResponse response) throws IOException {

            // 查询文件记录（已校验过，不会为null）
            String localFilePath = sf.getPath(); // 数据库中存储的完整本地路径
            String name = sf.getFilename(); // 文件名（用于响应头）
            File file = new File(localFilePath);

        String parentDirName = "";
        File parentDir = file.getParentFile().getParentFile();
        if (parentDir != null && parentDir.exists()) {
            parentDirName = parentDir.getName();
        }

        // 拼接新文件名：目录名_原始文件名（如"2024届简历_简历.doc"）
        // 兼容特殊情况：如果没有上一级目录（根路径文件），直接用原始文件名
        String filename = parentDirName.isEmpty()
                ? name
                : parentDirName + "-" + name;

            // 配置响应头（关键：决定浏览器“打开”而非“下载”）
            // 自动识别文件MIME类型（适配PDF、图片、音视频等所有格式）
            String contentType = Files.probeContentType(Paths.get(localFilePath));
            // 兜底：识别失败时用二进制流类型（浏览器尝试解析）
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            response.setContentType(contentType);

            // 配置为“浏览器内打开”（inline），处理中文文件名乱码
            String encodedFilename = java.net.URLEncoder.encode(filename, StandardCharsets.UTF_8);
            // filename*=UTF-8'' 是标准的中文编码格式，兼容所有现代浏览器
            response.setHeader("Content-Disposition",
                    String.format("inline; filename*=UTF-8''%s", encodedFilename));

            // 可选：设置文件大小（优化加载体验，大文件进度显示）
            response.setContentLengthLong(file.length());

            // 读取文件流，写入响应（核心步骤：传递文件流给前端）
            // 用try-with-resources自动关闭流，避免资源泄漏
            try (InputStream inputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[4096]; // 4KB缓冲区（高效传输，平衡内存和速度）
                int len;
                // 循环读取文件流并写入响应
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                // 刷新输出流，确保所有数据发送完成
                outputStream.flush();
            }
    }
}
