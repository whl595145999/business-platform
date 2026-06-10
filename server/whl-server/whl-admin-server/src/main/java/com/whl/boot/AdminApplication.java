package com.whl.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 供应链业务平台 — 管理端启动入口
 *
 * @author whl
 */
@SpringBootApplication(scanBasePackages = "com.whl")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AdminApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  供应链业务平台启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
