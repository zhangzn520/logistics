package com.egao.common.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Token工具类
 * Created by wangfan on 2018-1-21 下午4:30:59
 */
public class JwtUtil {
    // 这几个是国际通用，不建议修改
    public static final String TOKEN_TYPE = "Bearer";  // token认证类型
    public static final String TOKEN_HEADER_NAME = "Authorization";  // token在header中字段名称
    public static final String TOKEN_PARAM_NAME = "access_token";  // token在参数中字段名称

    /**
     * 获取请求中的access_token
     *
     * @param request HttpServletRequest
     * @return String
     */
    public static String getAccessToken(HttpServletRequest request) {
        String access_token = request.getParameter(TOKEN_PARAM_NAME);
        if (access_token == null || access_token.trim().isEmpty()) {
            access_token = request.getHeader(TOKEN_HEADER_NAME);
            if (access_token != null && access_token.startsWith(TOKEN_TYPE)) {
                access_token = access_token.substring(TOKEN_TYPE.length() + 1);
            }
        }
        return access_token;
    }

    /**
     * 生成token
     *
     * @param subject 载体
     * @param expire  过期时间
     * @param keyStr  base64格式密钥
     * @return String
     */
    public static String buildToken(String subject, Long expire, String keyStr) {
        return buildToken(subject, expire, parseKey(keyStr));
    }

    /**
     * 生成token
     *
     * @param subject 载体
     * @param expire  过期时间
     * @param key     密钥
     * @return String
     */
    public static String buildToken(String subject, Long expire, Key key) {
        Date expireDate = new Date(new Date().getTime() + 1000 * expire);
        return Jwts.builder().setSubject(subject).signWith(key).setExpiration(expireDate).compact();
    }

    /**
     * 解析token
     *
     * @param token  token
     * @param keyStr base64格式的key
     * @return Claims
     */
    public static Claims parseToken(String token, String keyStr) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(parseKey(keyStr)).parseClaimsJws(token);
        return claimsJws.getBody();
    }

    /**
     * 生成密钥Key
     *
     * @return Key
     */
    public static Key genKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    /**
     * 生成字符串(base64)形式的key
     *
     * @return String
     */
    public static String genKeyStr() {
        return Base64.getEncoder().encodeToString(genKey().getEncoded());
    }

    /**
     * 解析Key
     *
     * @param keyStr base64格式的key
     * @return Key
     */
    public static Key parseKey(String keyStr) {
        if (keyStr == null || keyStr.trim().isEmpty()) return null;
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(keyStr));
    }

}
