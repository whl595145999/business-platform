package com.whl.scm.product.adapter.admin.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类目默认策略 HTTP 出参（新建 SKU 预填）。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class CategoryDefaultsVo {

    private Integer defaultItemClass;
    private Integer defaultProductType;
    private Integer defaultExpiryFlag;
    private Integer defaultBarcodePolicy;
    private Integer defaultShelfLifeValue;
    private Integer defaultShelfLifeUnit;
    private Integer defaultNearExpiryValue;
    private Integer defaultNearExpiryUnit;

}
