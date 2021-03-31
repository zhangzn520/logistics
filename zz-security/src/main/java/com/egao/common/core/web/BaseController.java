package com.egao.common.core.web;

import com.egao.common.system.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Controller基类
 * Created by wangfan on 2017-06-10 10:10
 */
public class BaseController {

    /**
     * 获取当前登录的user
     */
    public User getLoginUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) return null;
            Object object = authentication.getPrincipal();
            if (object != null) return (User) object;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前登录的userId
     */
    public Integer getLoginUserId() {
        User loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getUserId();
    }

}
