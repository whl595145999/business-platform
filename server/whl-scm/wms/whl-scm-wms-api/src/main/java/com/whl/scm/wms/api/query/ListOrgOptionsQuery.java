package com.whl.scm.wms.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询组织档案下拉选项（租户维度全量）。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class ListOrgOptionsQuery {

    /** 租户编号 */
    private String tenantId;

    /** 状态过滤，默认 10 仅启用 */
    private Integer status;

}
