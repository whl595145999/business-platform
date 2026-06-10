package com.whl.platform.system.facade;

import com.whl.platform.api.user.UserFacade;
import com.whl.platform.api.user.UserSnapshot;
import com.whl.platform.system.domain.vo.SysUserVo;
import com.whl.platform.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * {@link UserFacade} 平台侧实现。
 */
@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final ISysUserService sysUserService;

    @Override
    public Optional<UserSnapshot> findById(Long userId) {
        return toSnapshot(sysUserService.selectUserById(userId));
    }

    @Override
    public Optional<UserSnapshot> findByUserName(String userName) {
        return toSnapshot(sysUserService.selectUserByUserName(userName));
    }

    private Optional<UserSnapshot> toSnapshot(SysUserVo vo) {
        if (vo == null) {
            return Optional.empty();
        }
        return Optional.of(new UserSnapshot(vo.getUserId(), vo.getUserName(), vo.getNickName(), vo.getDeptId()));
    }
}
