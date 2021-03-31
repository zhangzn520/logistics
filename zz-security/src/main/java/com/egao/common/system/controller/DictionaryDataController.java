package com.egao.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.egao.common.core.annotation.ApiPageParam;
import com.egao.common.core.annotation.OperLog;
import com.egao.common.core.web.*;
import com.egao.common.system.entity.DictionaryData;
import com.egao.common.system.service.DictionaryDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by wangfan on 2020-03-14 11:29:04
 */
@Api(tags = "字典项管理")
@RestController
@RequestMapping("/api/sys/dictdata")
public class DictionaryDataController extends BaseController {
    @Autowired
    private DictionaryDataService dictionaryDataService;

    @PreAuthorize("hasAuthority('sys:dict:list')")
    @OperLog(value = "字典项管理", desc = "分页查询")
    @ApiOperation("分页查询字典项")
    @ApiPageParam
    @GetMapping("/page")
    public PageResult<DictionaryData> page(HttpServletRequest request) {
        PageParam<DictionaryData> pageParam = new PageParam<>(request);
        return new PageResult<>(dictionaryDataService.listPage(pageParam), pageParam.getTotal());
    }

    @PreAuthorize("hasAuthority('sys:dict:list')")
    @OperLog(value = "字典项管理", desc = "查询全部")
    @ApiOperation("查询全部字典项")
    @GetMapping()
    public JsonResult list(HttpServletRequest request) {
        PageParam<DictionaryData> pageParam = new PageParam<>(request);
        List<DictionaryData> records = dictionaryDataService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.sortRecords(records));
    }

    @PreAuthorize("hasAuthority('sys:dict:list')")
    @OperLog(value = "字典项管理", desc = "根据id查询")
    @ApiOperation("根据id查询字典项")
    @GetMapping("/{id}")
    public JsonResult get(@PathVariable("id") Integer id) {
        PageParam<DictionaryData> pageParam = new PageParam<>();
        pageParam.put("dictDataId", id);
        List<DictionaryData> records = dictionaryDataService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.getOne(records));
    }

    @PreAuthorize("hasAuthority('sys:dict:save')")
    @OperLog(value = "字典项管理", desc = "添加", param = false, result = true)
    @ApiOperation("添加字典项")
    @PostMapping()
    public JsonResult add(@RequestBody DictionaryData dictionaryData) {
        if (dictionaryDataService.count(new QueryWrapper<DictionaryData>()
                .eq("dict_id", dictionaryData.getDictId())
                .eq("dict_data_name", dictionaryData.getDictDataName())) > 0) {
            return JsonResult.error("字典项名称已存在");
        }
        if (dictionaryDataService.count(new QueryWrapper<DictionaryData>()
                .eq("dict_id", dictionaryData.getDictId())
                .eq("dict_data_code", dictionaryData.getDictDataCode())) > 0) {
            return JsonResult.error("字典项标识已存在");
        }
        if (dictionaryDataService.save(dictionaryData)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @PreAuthorize("hasAuthority('sys:dict:update')")
    @OperLog(value = "字典项管理", desc = "修改", param = false, result = true)
    @ApiOperation("修改字典项")
    @PutMapping()
    public JsonResult update(@RequestBody DictionaryData dictionaryData) {
        if (dictionaryDataService.count(new QueryWrapper<DictionaryData>()
                .eq("dict_id", dictionaryData.getDictId())
                .eq("dict_data_name", dictionaryData.getDictDataName())
                .ne("dict_data_id", dictionaryData.getDictDataId())) > 0) {
            return JsonResult.error("字典项名称已存在");
        }
        if (dictionaryDataService.count(new QueryWrapper<DictionaryData>()
                .eq("dict_id", dictionaryData.getDictId())
                .eq("dict_data_code", dictionaryData.getDictDataCode())
                .ne("dict_data_id", dictionaryData.getDictDataId())) > 0) {
            return JsonResult.error("字典项标识已存在");
        }
        if (dictionaryDataService.updateById(dictionaryData)) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @PreAuthorize("hasAuthority('sys:dict:remove')")
    @OperLog(value = "字典项管理", desc = "删除", result = true)
    @ApiOperation("删除字典项")
    @DeleteMapping("/{id}")
    public JsonResult remove(@PathVariable("id") Integer id) {
        if (dictionaryDataService.removeById(id)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

    @PreAuthorize("hasAuthority('sys:dict:save')")
    @OperLog(value = "字典项管理", desc = "批量添加", param = false, result = true)
    @ApiOperation("批量添加字典项")
    @PostMapping("/batch")
    public JsonResult saveBatch(@RequestBody List<DictionaryData> dictDataList) {
        if (dictionaryDataService.saveBatch(dictDataList)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @PreAuthorize("hasAuthority('sys:dict:remove')")
    @OperLog(value = "字典项管理", desc = "批量删除", result = true)
    @ApiOperation("批量删除字典项")
    @DeleteMapping("/batch")
    public JsonResult removeBatch(@RequestBody List<Integer> ids) {
        if (dictionaryDataService.removeByIds(ids)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

}
