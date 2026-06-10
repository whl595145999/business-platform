package com.whl.scm.product.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除货品命令。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class DeleteSkuCommand {

    private String tenantId;
    private Long id;

}
