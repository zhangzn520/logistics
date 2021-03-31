package com.egao.common.system.controller;

import com.egao.common.core.annotation.ApiPageParam;
import com.egao.common.core.annotation.OperLog;
import com.egao.common.core.web.BaseController;
import com.egao.common.core.web.JsonResult;
import com.egao.common.core.web.PageParam;
import com.egao.common.core.web.PageResult;
import com.egao.common.system.entity.LoginRecord;
import com.egao.common.system.service.LoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by wangfan on 2018-12-24 16:10
 */
@Api(tags = "登录日志")
@RestController
@RequestMapping("/api/sys/loginRecord")
public class LoginRecordController extends BaseController {
    @Autowired
    private LoginRecordService loginRecordService;

    @PreAuthorize("hasAuthority('sys:login_record:view')")
    @OperLog(value = "登录日志", desc = "分页查询")
    @ApiOperation("分页查询登录日志")
    @ApiPageParam
    @GetMapping("/page")
    public PageResult<LoginRecord> page(HttpServletRequest request) {
        PageParam<LoginRecord> pageParam = new PageParam<>(request);
        pageParam.setDefaultOrder(null, new String[]{"create_time"});
        return new PageResult<>(loginRecordService.listPage(pageParam), pageParam.getTotal());
    }

    @PreAuthorize("hasAuthority('sys:login_record:view')")
    @OperLog(value = "登录日志", desc = "查询全部")
    @ApiOperation("查询全部登录日志")
    @GetMapping()
    public JsonResult list(HttpServletRequest request) {
        PageParam<LoginRecord> pageParam = new PageParam<>(request);
        List<LoginRecord> records = loginRecordService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.sortRecords(records));
    }

    @PreAuthorize("hasAuthority('sys:login_record:view')")
    @OperLog(value = "登录日志", desc = "根据id查询")
    @ApiOperation("根据id查询登录日志")
    @GetMapping("/{id}")
    public JsonResult get(@PathVariable("id") Integer id) {
        PageParam<LoginRecord> pageParam = new PageParam<>();
        pageParam.put("id", id);
        List<LoginRecord> records = loginRecordService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.getOne(records));
    }

}
