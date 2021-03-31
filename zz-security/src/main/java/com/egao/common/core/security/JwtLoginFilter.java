package com.egao.common.core.security;

import com.alibaba.fastjson.JSON;
import com.egao.common.core.Constants;
import com.egao.common.core.web.JsonResult;
import com.egao.common.system.entity.LoginRecord;
import com.egao.common.system.service.LoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录过滤器
 * Created by wangfan on 2020-03-24 12:57
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private LoginRecordService loginRecordService;

    public JwtLoginFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        super.setFilterProcessesUrl("/api/login");
    }

    /**
     * 登录成功签发token返回json数据
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        UserDetails user = (UserDetails) authResult.getPrincipal();
        String access_token = JwtUtil.buildToken(user.getUsername(), Constants.TOKEN_EXPIRE_TIME, Constants.TOKEN_KEY);
        // 记录登录日志
        loginRecordService.saveAsync(user.getUsername(), LoginRecord.TYPE_LOGIN, null, request);
        // 返回json数据
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(JsonResult.ok("登录成功").put("access_token", access_token)
                .put("token_type", JwtUtil.TOKEN_TYPE)));
        out.flush();
    }

    /**
     * 登录失败处理
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException e) throws IOException, ServletException {
        String username = request.getParameter("username");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonResult result;
        if (e instanceof UsernameNotFoundException) {
            result = JsonResult.error("账号不存在");
            loginRecordService.saveAsync(username, LoginRecord.TYPE_ERROR, "账号不存在", request);
        } else if (e instanceof BadCredentialsException) {
            result = JsonResult.error("账号或密码错误");
            loginRecordService.saveAsync(username, LoginRecord.TYPE_ERROR, "账号或密码错误", request);
        } else if (e instanceof LockedException) {
            result = JsonResult.error("账号被锁定");
            loginRecordService.saveAsync(username, LoginRecord.TYPE_ERROR, "账号被锁定", request);
        } else {
            result = JsonResult.error(e.getMessage());
        }
        out.write(JSON.toJSONString(result));
        out.flush();
    }

}
