package com.whl.scm.product.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 货品条码 DTO，对应表 {@code prd_sku_barcode}。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class SkuBarcodeDto {

    /** 主键 */
    private Long id;

    /** 条码值，租户内全局唯一 */
    private String barcode;

    /** 是否默认扫码：1-是,0-否 */
    private Integer defaultFlag;

    /** 启停：1-启用,0-停用 */
    private Integer status;

    /** 备注 */
    private String remark;

}
