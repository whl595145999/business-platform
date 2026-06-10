package com.whl.scm.product.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 按业务键查询货品类目。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class GetCategoryQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private Long id;

}
