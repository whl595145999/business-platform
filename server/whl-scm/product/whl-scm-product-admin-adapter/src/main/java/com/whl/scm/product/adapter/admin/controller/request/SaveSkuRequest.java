package com.whl.scm.product.adapter.admin.controller.request;

import com.whl.scm.product.adapter.admin.controller.vo.SkuBarcodeVo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 保存货品 HTTP 请求体。
 *
 * @author whl
 */
@Data
public class SaveSkuRequest {

    private String tenantId;

    @NotBlank(message = "货品编码不能为空", groups = SkuValidationGroups.Create.class)
    private String skuCode;

    @NotBlank(message = "货品名称不能为空", groups = {SkuValidationGroups.Create.class, SkuValidationGroups.Update.class})
    private String skuName;

    private String skuShortName;
    private String spec;

    @NotNull(message = "状态不能为空", groups = {SkuValidationGroups.Create.class, SkuValidationGroups.Update.class})
    private Integer status;

    private Integer sortOrder;
    private String remark;

    @NotNull(message = "类目不能为空", groups = {SkuValidationGroups.Create.class, SkuValidationGroups.Update.class})
    private Long categoryId;

    private Long brandId;

    @NotBlank(message = "计量单位不能为空", groups = {SkuValidationGroups.Create.class, SkuValidationGroups.Update.class})
    private String unitCode;

    @NotNull(message = "货品类型不能为空", groups = {SkuValidationGroups.Create.class, SkuValidationGroups.Update.class})
    private Integer itemClass;

    private Integer purchaseAllowed;
    private Integer saleAllowed;
    private Integer issueAllowed;

    @NotNull(message = "履约类型不能为空", groups = {SkuValidationGroups.Create.class, SkuValidationGroups.Update.class})
    private Integer productType;

    private Integer expiryFlag;
    private Integer quotaFlag;
    private Integer shelfLifeValue;
    private Integer shelfLifeUnit;
    private Integer nearExpiryValue;
    private Integer nearExpiryUnit;
    private String defaultOriginCode;
    private String taxCategoryCode;
    private String inputTaxRate;
    private String outputTaxRate;
    private Integer barcodePolicy;
    private List<SkuBarcodeVo> barcodes;

}
