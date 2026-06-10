package com.whl.boot.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.whl.framework.core.utils.SpringUtils;
import com.whl.framework.core.utils.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OpenAPI 进程首页
 */
@SaIgnore
@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return StringUtils.format("欢迎使用{} OpenAPI 服务。认证：/openapi/auth/login，业务：/openapi/scm/**", SpringUtils.getApplicationName());
    }

}
