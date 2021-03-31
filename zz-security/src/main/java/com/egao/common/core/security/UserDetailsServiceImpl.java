package com.egao.common.core.security;

import com.egao.common.system.entity.User;
import com.egao.common.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 用户登录验证Service
 * Created by wangfan on 2020-03-23 18:45
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        if (user == null) throw new UsernameNotFoundException("Username Not Found");
        return userService.selectUserAuth(user);
    }

}