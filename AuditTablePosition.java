package com.ruoyi.oacommon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AuditTablePosition implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 审批节点名称
     */
    private String taskName;
    
    /**
     * 审批节点签章的y坐标
     */
    private float y;
    
    /**
     * 当前节点的审批次数
     */
    private int counter;
}
