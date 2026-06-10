package com.whl.platform.api.user;

import java.util.Optional;

/**
 * 用户 Facade：SCM 域通过本接口访问平台用户，禁止直接依赖 {@code ISysUserService}。
 */
public interface UserFacade {

    Optional<UserSnapshot> findById(Long userId);

    Optional<UserSnapshot> findByUserName(String userName);
}
