package com.ruoyi.oacommon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 签章操作信息
 */
@Data
@AllArgsConstructor
public class DigitalStampOpInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 印章在页面上的位置
     */
    private List<AuditTablePosition> auditTablePositionList;
}
