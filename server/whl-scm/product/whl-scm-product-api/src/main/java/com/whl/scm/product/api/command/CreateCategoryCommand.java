package com.whl.scm.product.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建货品类目命令。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class CreateCategoryCommand {

    private String tenantId;
    private String categoryCode;
    private String categoryName;
    private Long parentId;
    private Integer status;
    private Integer sortOrder;
    private String remark;
    private Integer defaultItemClass;
    private Integer defaultProductType;
    private Integer defaultExpiryFlag;
    private Integer defaultBarcodePolicy;
    private Integer defaultShelfLifeValue;
    private Integer defaultShelfLifeUnit;
    private Integer defaultNearExpiryValue;
    private Integer defaultNearExpiryUnit;

}
