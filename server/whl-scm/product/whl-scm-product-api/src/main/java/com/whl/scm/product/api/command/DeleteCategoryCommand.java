package com.whl.scm.product.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除货品类目命令。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class DeleteCategoryCommand {

    private String tenantId;
    private Long id;

}
