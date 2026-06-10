package com.whl.scm.product.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 条码解析结果。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class ResolveBarcodeResult {

    /** 货品编码 */
    private String skuCode;

}
