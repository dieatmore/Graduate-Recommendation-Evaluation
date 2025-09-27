package org.example.graduaterecommendationevaluation.service;

import lombok.RequiredArgsConstructor;
import org.example.graduaterecommendationevaluation.dox.TargetNode;
import org.example.graduaterecommendationevaluation.dto.TargetNodeTreeDTO;
import org.example.graduaterecommendationevaluation.exception.Code;
import org.example.graduaterecommendationevaluation.exception.XException;
import org.example.graduaterecommendationevaluation.repository.TargetNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TargetService {
    private final TargetNodeRepository targetNodeRepository;

    // 添加指标节点信息
    @Transactional
    public void addTargetNode(TargetNode targetNode) {
        targetNodeRepository.save(targetNode);
    }

    // 查看指标节点
//    public List<TargetNodeTreeDTO> listTargetNodeTree(Long categoryId) {
//        List<TargetNode> allNodes = targetNodeRepository.findAllByCategoryIdRecursive(categoryId);
//        if (allNodes.isEmpty()) {
//            return null;
//        }
//    }
}
