package com.whl.scm.product.adapter.admin.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 保存货品类目 HTTP 请求体。
 *
 * @author whl
 */
@Data
public class SaveCategoryRequest {

    private String tenantId;

    @NotBlank(message = "类目编码不能为空", groups = CategoryValidationGroups.Create.class)
    private String categoryCode;

    @NotBlank(message = "类目名称不能为空", groups = {CategoryValidationGroups.Create.class, CategoryValidationGroups.Update.class})
    private String categoryName;

    private Long parentId;

    @NotNull(message = "状态不能为空", groups = {CategoryValidationGroups.Create.class, CategoryValidationGroups.Update.class})
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
