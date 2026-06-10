package com.whl.scm.product.api.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 货品类目树查询。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class ListCategoryTreeQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private Integer status;

}
