package org.example.graduaterecommendationevaluation.controller;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.Category;
import org.example.graduaterecommendationevaluation.dox.Score;
import org.example.graduaterecommendationevaluation.dox.TargetNode;
import org.example.graduaterecommendationevaluation.dox.TargetSubmit;
import org.example.graduaterecommendationevaluation.dto.SubmitDTO;
import org.example.graduaterecommendationevaluation.dto.TargetNodeTreeDTO;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.service.TargetService;
import org.example.graduaterecommendationevaluation.service.UserService;
import org.example.graduaterecommendationevaluation.vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/")
public class StudentController {

    private final TargetService targetService;
    private final UserService userService;

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
        return ResultVO.success(targetService.changeTree(children, parentId));
    }

    // 学生删除已提交状态的指标提交
    @DeleteMapping("nodes/{rootId}/submits/{submitId}")
    public ResultVO deleteNode(@PathVariable("rootId") Long rootId,
                               @PathVariable("submitId") Long submitId,
                               @RequestAttribute("catId") Long catId,
                               @RequestAttribute("uid") Long uid) {
        targetService.judgeNode(rootId, catId);
        targetService.judgeRoot(rootId);
        TargetSubmit ts = targetService.getSubmitById(submitId);
        if(!ts.getUserId().equals(uid)){
            return ResultVO.error(Code.ERROR, "该节点不存在！");
        }
        if(!ts.getStatus().equals(TargetSubmit.SUBMIT)) {
            return ResultVO.error(Code.ERROR, "该节点不可删除！");
        }
        targetService.deleteSubmit(ts);
        return ResultVO.ok();
    }

    // 学生新增指标提交 ( ToDo：是否可重复提交？限项？ )
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

    // 学生 提交/更改 加权成绩信息
    @PostMapping("score")
    public ResultVO submitScore(@RequestAttribute("uid") Long uid,
                                @RequestBody Score score) {
        Score s = userService.getScoreByUid(uid);
        if(s==null){
            score.setUserId(uid);
            score.setStatus((short)0);
            userService.submitScore(score);
        } else if(s.getStatus() == 0) {
            s.setScorex(score.getScorex());
            s.setRanking(score.getRanking());
            userService.submitScore(s);
        } else {
            return ResultVO.error(Code.ERROR, "成绩已认定，不可更改！");
        }
        return ResultVO.ok();
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
