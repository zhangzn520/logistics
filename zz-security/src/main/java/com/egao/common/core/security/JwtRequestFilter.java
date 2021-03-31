package com.egao.common.core.security;

import com.egao.common.core.Constants;
import com.egao.common.system.entity.LoginRecord;
import com.egao.common.system.service.LoginRecordService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 请求过滤器,处理携带token的请求
 * Created by wangfan on 2020-03-23 22:46
 */
public class JwtRequestFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    @Autowired
    private LoginRecordService loginRecordService;

    public JwtRequestFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String access_token = JwtUtil.getAccessToken(request);
        if (access_token != null) {
            try {
                Claims claims = JwtUtil.parseToken(access_token, Constants.TOKEN_KEY);
                String username = claims.getSubject();
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // token将要过期签发新token, 防止突然退出登录
                if ((claims.getExpiration().getTime() - new Date().getTime()) / 1000 / 60 < Constants.TOKEN_WILL_EXPIRE) {
                    String access_token_new = JwtUtil.buildToken(username, Constants.TOKEN_EXPIRE_TIME, Constants.TOKEN_KEY);
                    response.addHeader(JwtUtil.TOKEN_HEADER_NAME, access_token_new);
                    loginRecordService.saveAsync(username, LoginRecord.TYPE_REFRESH, null, request);
                }
            }/* catch (ExpiredJwtException e) {
                System.out.println(e.getMessage());
                throw new AccountExpiredException(e.getMessage(), e.getCause());
            }*/ catch (Exception e) {
                System.out.println(e.getMessage());
                // throw new BadCredentialsException(e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }

}
