package org.example.graduaterecommendationevaluation.service;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.TargetNode;
import org.example.graduaterecommendationevaluation.dox.TargetSubmit;
import org.example.graduaterecommendationevaluation.dox.User;
import org.example.graduaterecommendationevaluation.dto.FileDTO;
import org.example.graduaterecommendationevaluation.dto.SubmitDTO;
import org.example.graduaterecommendationevaluation.dto.TargetNodeTreeDTO;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.TargetNodeRepository;
import org.example.graduaterecommendationevaluation.repository.TargetSubmitRepository;
import org.example.graduaterecommendationevaluation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TargetService {
    private final TargetNodeRepository targetNodeRepository;
    private final TargetSubmitRepository targetSubmitRepository;
    private final UserRepository userRepository;

    // 添加指标节点信息
    @Transactional
    public void addTargetNode(TargetNode targetNode) {
        targetNodeRepository.save(targetNode);
    }



    // 查看所有指标节点(树)
    public List<TargetNodeTreeDTO> listTargetNodeTree(Long categoryId) {
        List<TargetNode> allNodes = targetNodeRepository.findAllByCategoryIdRecursive(categoryId);
        if (allNodes.isEmpty()) {
            return new ArrayList<>();
        }
        return changeTree(allNodes, null);
    }

    // 转换成树形
    public List<TargetNodeTreeDTO> changeTree(List<TargetNode> allNodes, Long rootId) {
        // 转换节点为DTO
        List<TargetNodeTreeDTO> allDtos = allNodes.stream()
                .map(this::convertToTreeDTO)
                .toList();

        // 构建ID到DTO的map，快速查找
        Map<Long, TargetNodeTreeDTO> dtoMap = allDtos.stream()
                .collect(Collectors.toMap(TargetNodeTreeDTO::getId, dto -> dto));

        // 拼
        List<TargetNodeTreeDTO> result = new ArrayList<>();
        for (TargetNodeTreeDTO dto : allDtos) {
            Long parentId = dto.getParentId();
            if (parentId == null || parentId.equals(rootId)) {
                // 根节点
                result.add(dto);
            } else {
                // 找到父节点并添加到其子节点列表
                TargetNodeTreeDTO parentDto = dtoMap.get(parentId);
                if (parentDto != null) {
                    if (parentDto.getChildren() == null) {
                        parentDto.setChildren(new ArrayList<>());
                    }
                    parentDto.getChildren().add(dto);
                }
            }
        }

        return result;
    }

    // 转换实体类到DTO
    public TargetNodeTreeDTO convertToTreeDTO(TargetNode node) {
        return TargetNodeTreeDTO.builder()
                .id(node.getId())
                .parentId(node.getParentId())
                .name(node.getName())
                .categoryId(node.getCategoryId())
                .maxMark(node.getMaxMark())
                .maxNumber(node.getMaxNumber())
                .comment(node.getComment())
                .children(null)
                .build();
    }

    // 删除List规则节点
    @Transactional
    public void deleteTargetNodes(List<TargetNode> targetNodes) {
        targetNodeRepository.deleteAll(targetNodes);
    }

    // 学生查看自己所在类别的根节点
    public List<TargetNode> listRootNodes(Long catId) {
        List<TargetNode> allNodes = targetNodeRepository.findRootByCategoryId(catId);
        if (allNodes.isEmpty()) {
            return new ArrayList<>();
        }
        return allNodes;
    }

    // 判断节点权限
    public void judgeNode(Long nodeId, Long catId) {
        TargetNode t = targetNodeRepository.findByIdAndCategoryId(nodeId, catId);
        if(t==null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("该节点不存在！")
                    .build();
        }
    }

    // 根据父节点id查找所有子节点
    public List<TargetNode> listChildrenNodes(Long catId, Long parentId) {
        return targetNodeRepository.findChildrenByCatIdAndParentId(catId, parentId);
    }

    // 判断一个节点是不是叶子节点
    public int judgeLeaf(Long nodeId) {
        return targetNodeRepository.judgeLeaf(nodeId);
    }

    // 判断一个节点是不是根节点
    public void judgeRoot(Long nodeId) {
        TargetNode t = targetNodeRepository.findById(nodeId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("该节点不存在！")
                        .build());
        if(t.getParentId() != null){
            throw XException.builder()
                    .number(Code.ERROR)
                    .message("根节点错误！")
                    .build();
        }
    }

    // 根据id获取节点
    public TargetNode getNodeById(Long nodeId) {
        return targetNodeRepository.findById(nodeId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("该节点不存在！")
                        .build());
    }

    // 添加指标节点提交
    @Transactional
    public void addSubmit(TargetSubmit targetSubmit) {
        targetSubmitRepository.save(targetSubmit);
    }

    // 删除提交节点（submit）
    @Transactional
    public void deleteSubmit(TargetSubmit targetSubmit) {
        targetSubmitRepository.delete(targetSubmit);
    }

    // 根据id获取指标提交信息
    public TargetSubmit getSubmitById(Long submitId) {
        return targetSubmitRepository.findById(submitId)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("该节点不存在！")
                        .build());
    }

    // 根据根节点id获取学生所有的提交信息
    public List<SubmitDTO> listSubmits(Long rootId, Long uid) {
        return targetSubmitRepository.listSubmitAndFiles(rootId, uid);
    }

    // 给提交项评分
    @Transactional
    public void submitMark(Long uid, TargetSubmit targetSubmit,Long submitId) {
        User u = userRepository.findById(uid)
                .orElseThrow(() -> XException.builder()
                        .number(Code.ERROR)
                        .message("不存在该用户！")
                        .build());
        targetSubmitRepository.submitMark(
                submitId, u.getName(), targetSubmit.getMark(),targetSubmit.getComment(),targetSubmit.getStatus());
    }
}
