package com.egao;

import com.egao.common.core.security.JwtUtil;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by wangfan on 2020-03-23 23:37
 */
public class TestMain {

    @Test
    public void testPsw() {
        // System.out.println(new BCryptPasswordEncoder().encode("admin"));
    }

    @Test
    public void testKey() {
        // System.out.println(JwtUtil.genKeyStr());
    }

}
