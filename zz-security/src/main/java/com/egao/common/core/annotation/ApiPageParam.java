package com.egao.common.core.annotation;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分页查询接口统一参数
 * Created by wangfan on 2020-01-13 14:13
 */
@ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "第几页", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "limit", value = "每页多少条", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "sort", value = "排序字段", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "order", value = "排序方式", dataType = "string", paramType = "query")
})
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPageParam {
}
