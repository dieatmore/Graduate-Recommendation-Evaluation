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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<TargetNodeTreeDTO> listTargetNodeTree(Long categoryId) {
        List<TargetNode> allNodes = targetNodeRepository.findAllByCategoryIdRecursive(categoryId);
        if (allNodes.isEmpty()) {
            return null;
        }
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
            if (parentId == null) {
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
    private TargetNodeTreeDTO convertToTreeDTO(TargetNode node) {
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
}
