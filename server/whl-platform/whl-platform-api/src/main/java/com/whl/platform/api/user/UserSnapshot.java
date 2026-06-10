package com.whl.platform.api.user;

/**
 * 平台用户快照（SCM 等域可读，不含密码等敏感字段）。
 */
public record UserSnapshot(
    Long userId,
    String userName,
    String nickName,
    Long deptId
) {
}
