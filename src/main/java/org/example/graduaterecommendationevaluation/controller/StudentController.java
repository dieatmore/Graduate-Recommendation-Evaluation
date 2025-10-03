package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.TargetNode;
import org.example.graduaterecommendationevaluation.dox.TargetSubmit;
import org.example.graduaterecommendationevaluation.dto.SubmitDTO;
import org.example.graduaterecommendationevaluation.dto.TargetNodeTreeDTO;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.service.TargetService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/")
public class StudentController {

    private final TargetService targetService;

    // 查看自己所在类别的所有根节点
    @GetMapping("nodes")
    public ResultVO getRootNodes(@RequestAttribute("catId") Long catId) {
        List<TargetNode> root = targetService.listRootNodes(catId);
        if (root.isEmpty()) {
            return ResultVO.error(Code.ERROR, "无第一级指标！");
        }
        return ResultVO.success(root);
    }

    // 根据父节点id查找所有子节点
    @GetMapping("childrenNodes/{parentId}")
    public ResultVO getChildrenNodes(@PathVariable("parentId") Long parentId,
                                     @RequestAttribute("catId") Long catId) {
        targetService.judgeNode(parentId, catId);
        List<TargetNode> children = targetService.listChildrenNodes(catId, parentId);
        if (children.isEmpty()) {
            return ResultVO.error(Code.ERROR, "无对应规则指标！");
        }
        targetService.changeTree(children, parentId);
        return ResultVO.success(children);
    }

    // 学生新增指标提交
    @PostMapping("nodes/{rootId}/submits/{nodeId}")
    public ResultVO addSubmit(@PathVariable("rootId") Long rootId,
                              @PathVariable("nodeId") Long nodeId,
                              @RequestAttribute("catId") Long catId,
                              @RequestAttribute("uid") Long uid) {
        targetService.judgeNode(nodeId, catId);
        targetService.judgeNode(rootId, catId);
        targetService.judgeRoot(rootId);
        if(targetService.judgeLeaf(nodeId) != 0){
            return  ResultVO.error(Code.ERROR, "该节点不可提交！");
        }
        List<TargetNode> children = targetService.listChildrenNodes(catId, rootId);
        if (children.isEmpty()) {
            return ResultVO.error(Code.ERROR, "不存在该节点！");
        }
        boolean rootContain = children.stream()
                .anyMatch(tn -> tn.getId().equals(nodeId));
        if(!rootContain){
            return ResultVO.error(Code.ERROR, "该节点错误！");
        }
        TargetNode thisNode = targetService.getNodeById(nodeId);
        TargetNode parentNode = targetService.getNodeById(thisNode.getParentId());
        TargetSubmit ts = TargetSubmit.builder()
                .userId(uid)
                .targetNodeId(nodeId)
                .rootNodeId(rootId)
                .name(parentNode.getName() + " — " + thisNode.getName())
                .status(TargetSubmit.SUBMIT)
                .record("[]")
                .build();
        targetService.addSubmit(ts);
        return ResultVO.ok();
    }

    // 学生根据根节点id获取所有的提交信息
    @GetMapping("nodes/{rootId}/submits")
    public ResultVO listSubmits(@PathVariable("rootId") Long rootId,
                                       @RequestAttribute("catId") Long catId,
                                       @RequestAttribute("uid") Long uid) {
        targetService.judgeNode(rootId, catId);
        return ResultVO.success(targetService.listSubmits(rootId,uid));
    }





    // 学生新增文件上传
//    @PostMapping("file/{submitNodeId}")
//    public ResultVO addFile(@PathVariable("submitNodeId") Long submitNodeId,
//                            @RequestAttribute("uid") Long uid) {
//        TargetSubmit thisSubmit = targetService.getSubmitById(submitNodeId);
//        if(!uid.equals(thisSubmit.getUserId())) {
//            return ResultVO.error(Code.ERROR, "不存在该节点！");
//        }
//    }

}
