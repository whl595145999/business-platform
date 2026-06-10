package com.whl.scm.product.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除品牌命令。
 *
 * @author whl
 */
@Data
@NoArgsConstructor
public class DeleteBrandCommand {

    private String tenantId;
    private Long id;

}
