package com.whl.scm.product.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 跨域引用前校验货品。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class ValidateSkuQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private String skuCode;
    /** 可选：PURCHASE / SALE / ISSUE */
    private String requiredCapability;

}
