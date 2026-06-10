package com.whl.scm.wms.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除组织档案命令。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class DeleteOrgCommand {

    /** 租户编号 */
    private String tenantId;

    /** 主键 */
    private Long id;

}
