package com.whl.scm.product.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 按业务键查询品牌。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class GetBrandQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private Long id;

}
