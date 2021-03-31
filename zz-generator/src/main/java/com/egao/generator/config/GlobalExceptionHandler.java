package com.egao.generator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * Created by wangfan on 2018-02-22 11:29
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Map errorHandler(Exception ex) {
        logger.error(ex.getMessage(), ex);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 1);
        map.put("msg", ex.getMessage());
        map.put("error", ex.toString());
        return map;
    }

}
