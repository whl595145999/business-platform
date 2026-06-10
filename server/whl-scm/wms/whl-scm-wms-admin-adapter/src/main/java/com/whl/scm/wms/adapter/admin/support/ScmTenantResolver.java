package com.whl.scm.wms.adapter.admin.support;

import cn.hutool.core.util.StrUtil;
import com.whl.framework.satoken.utils.LoginHelper;
import com.whl.framework.tenant.helper.TenantHelper;

/**
 * SCM 管理端租户解析：普通用户取登录租户；超级管理员可指定租户。
 *
 * @author whl
 */
public final class ScmTenantResolver {

    private ScmTenantResolver() {
    }

    /**
     * 解析有效租户编号。
     * <p>
     * 超级管理员且传入 {@code requestTenantId} 时使用请求值；否则使用 {@link TenantHelper#getTenantId()}。
     *
     * @param requestTenantId 前端传入的租户编号，可为 null
     * @return 有效租户编号
     */
    public static String resolve(String requestTenantId) {
        if (LoginHelper.isSuperAdmin() && StrUtil.isNotBlank(requestTenantId)) {
            return requestTenantId.trim();
        }
        return TenantHelper.getTenantId();
    }

}
