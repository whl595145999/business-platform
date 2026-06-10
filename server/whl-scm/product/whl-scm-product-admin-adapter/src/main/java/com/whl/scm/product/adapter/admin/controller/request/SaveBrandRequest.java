package com.whl.scm.product.adapter.admin.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 保存品牌 HTTP 请求体。
 *
 * @author whl
 */
@Data
public class SaveBrandRequest {

    private String tenantId;

    @NotBlank(message = "品牌编码不能为空", groups = BrandValidationGroups.Create.class)
    private String brandCode;

    @NotBlank(message = "品牌名称不能为空", groups = {BrandValidationGroups.Create.class, BrandValidationGroups.Update.class})
    private String brandName;

    @NotNull(message = "状态不能为空", groups = {BrandValidationGroups.Create.class, BrandValidationGroups.Update.class})
    private Integer status;

    private Integer sortOrder;
    private String remark;

}
