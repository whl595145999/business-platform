package com.whl.framework.translation.core.impl;

import cn.hutool.core.convert.Convert;
import com.whl.framework.core.service.UserService;
import com.whl.framework.translation.annotation.TranslationType;
import com.whl.framework.translation.constant.TransConstant;
import com.whl.framework.translation.core.TranslationInterface;
import lombok.AllArgsConstructor;

/**
 * 用户名翻译实现
 *
 * @author Lion Li
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl implements TranslationInterface<String> {

    private final UserService userService;

    @Override
    public String translation(Object key, String other) {
        return userService.selectUserNameById(Convert.toLong(key));
    }
}
