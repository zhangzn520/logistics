package com.egao.common.core.security;

import com.alibaba.fastjson.JSON;
import com.egao.common.core.web.JsonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 没有权限异常处理
 * Created by wangfan on 2020-03-25 0:35
 */
public class JwtExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

    /**
     * 已登录访问被拒异常处理
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException, ServletException {
        doHandler(request, response, e);
    }

    /**
     * 未登录访问被拒异常处理
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException {
        doHandler(request, response, e);
    }

    private void doHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonResult result;
        if (e instanceof AccessDeniedException) {
            result = JsonResult.error(403, "没有访问权限");
        } else if (e instanceof InsufficientAuthenticationException) {
            result = JsonResult.error(401, "未登录");
        } else if (e instanceof AccountExpiredException) {
            result = JsonResult.error(401, "登录已过期");
        } else {
            result = JsonResult.error(401, "未登录或登录已过期").put("error", e.toString());
        }
        out.write(JSON.toJSONString(result));
        out.flush();
    }

}
