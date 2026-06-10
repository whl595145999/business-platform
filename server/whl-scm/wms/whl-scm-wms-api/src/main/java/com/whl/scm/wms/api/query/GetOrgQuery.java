package com.whl.scm.wms.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按业务键查询组织档案。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class GetOrgQuery {

    /** 租户编号 */
    private String tenantId;

    /** 主键 */
    private Long id;

}
