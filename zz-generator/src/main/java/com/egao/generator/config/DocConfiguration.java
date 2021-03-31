package com.egao.generator.config;

import com.egao.generator.expand.GenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 开发文档配置
 */
@Configuration
public class DocConfiguration implements WebMvcConfigurer {
    @Value("${gen.cache}")
    private Integer cache;

    /**
     * 文档位置映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc/**").addResourceLocations("file:///" + new GenUtil(cache).getBaseDir() + "doc/");
    }

}