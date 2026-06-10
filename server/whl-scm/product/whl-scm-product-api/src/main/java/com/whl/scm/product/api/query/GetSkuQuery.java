package com.whl.scm.product.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 按业务键查询货品（skuId 与 skuCode 二选一）。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class GetSkuQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private Long skuId;
    private String skuCode;

}
