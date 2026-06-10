package com.whl.framework.kernel.error;

/**
 * SCM 域错误码契约，格式：{域}{3位数字}。
 */
public interface ScmErrorCode {

    String code();

    String message();

}
