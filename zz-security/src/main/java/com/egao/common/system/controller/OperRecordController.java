package com.egao.common.system.controller;

import com.egao.common.core.annotation.ApiPageParam;
import com.egao.common.core.annotation.OperLog;
import com.egao.common.core.web.BaseController;
import com.egao.common.core.web.JsonResult;
import com.egao.common.core.web.PageParam;
import com.egao.common.core.web.PageResult;
import com.egao.common.system.entity.OperRecord;
import com.egao.common.system.service.OperRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by wangfan on 2018-12-24 16:10
 */
@Api(tags = "操作日志")
@RestController
@RequestMapping("/api/sys/operRecord")
public class OperRecordController extends BaseController {
    @Autowired
    private OperRecordService operLogService;

    /**
     * 分页查询操作日志
     */
    @PreAuthorize("hasAuthority('sys:oper_record:view')")
    @OperLog(value = "操作日志", desc = "分页查询")
    @ApiOperation("分页查询操作日志")
    @ApiPageParam
    @GetMapping("/page")
    public PageResult<OperRecord> page(HttpServletRequest request) {
        PageParam<OperRecord> pageParam = new PageParam<>(request);
        pageParam.setDefaultOrder(null, new String[]{"create_time"});
        return new PageResult<>(operLogService.listPage(pageParam), pageParam.getTotal());
    }

    /**
     * 查询全部操作日志
     */
    @PreAuthorize("hasAuthority('sys:oper_record:view')")
    @OperLog(value = "操作日志", desc = "查询全部")
    @ApiOperation("查询全部操作日志")
    @GetMapping()
    public JsonResult list(HttpServletRequest request) {
        PageParam<OperRecord> pageParam = new PageParam<>(request);
        List<OperRecord> records = operLogService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.sortRecords(records));
    }

    /**
     * 根据id查询操作日志
     */
    @PreAuthorize("hasAuthority('sys:oper_record:view')")
    @OperLog(value = "操作日志", desc = "根据id查询")
    @ApiOperation("根据id查询操作日志")
    @GetMapping("/{id}")
    public JsonResult get(@PathVariable("id") Integer id) {
        PageParam<OperRecord> pageParam = new PageParam<>();
        pageParam.put("id", id);
        List<OperRecord> records = operLogService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.getOne(records));
    }

}
