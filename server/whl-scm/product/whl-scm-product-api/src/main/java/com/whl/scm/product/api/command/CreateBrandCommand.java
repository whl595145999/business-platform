package com.whl.scm.product.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建品牌命令。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class CreateBrandCommand {

    private String tenantId;
    private String brandCode;
    private String brandName;
    private Integer status;
    private Integer sortOrder;
    private String remark;

}
