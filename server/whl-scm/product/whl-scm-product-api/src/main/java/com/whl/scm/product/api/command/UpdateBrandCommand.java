package com.whl.scm.product.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新品牌命令。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class UpdateBrandCommand {

    private String tenantId;
    private Long id;
    private String brandName;
    private Integer status;
    private Integer sortOrder;
    private String remark;

}
