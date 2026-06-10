package com.whl.scm.product.adapter.admin.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 货品条码 HTTP 出参。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class SkuBarcodeVo {

    private Long id;
    private String barcode;
    private Integer defaultFlag;
    private Integer status;
    private String remark;

}
