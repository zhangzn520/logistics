package com.egao.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.egao.common.core.annotation.ApiPageParam;
import com.egao.common.core.web.*;
import com.egao.common.core.utils.CoreUtil;
import com.egao.common.system.entity.Dictionary;
import com.egao.common.system.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wangfan on 2020-03-14 11:29:03
 */
@Api(tags = "字典管理")
@RestController
@RequestMapping("/api/sys/dict")
public class DictionaryController extends BaseController {
    @Autowired
    private DictionaryService dictionaryService;

    @PreAuthorize("hasAuthority('sys:dict:list')")
    @ApiOperation("分页查询字典")
    @ApiPageParam
    @GetMapping("/page")
    public PageResult<Dictionary> page(HttpServletRequest request) {
        PageParam<Dictionary> pageParam = new PageParam<>(request);
        return new PageResult<>(dictionaryService.page(pageParam, pageParam.getWrapper()).getRecords(), pageParam.getTotal());
    }

    @PreAuthorize("hasAuthority('sys:dict:list')")
    @ApiOperation("查询全部字典")
    @GetMapping()
    public JsonResult list(HttpServletRequest request) {
        PageParam<Dictionary> pageParam = new PageParam<>(request);
        return JsonResult.ok().setData(dictionaryService.list(pageParam.getOrderWrapper()));
    }

    @PreAuthorize("hasAuthority('sys:dict:list')")
    @ApiOperation("根据id查询字典")
    @GetMapping("/{id}")
    public JsonResult get(@PathVariable("id") Integer id) {
        return JsonResult.ok().setData(dictionaryService.getById(id));
    }

    @PreAuthorize("hasAuthority('sys:dict:save')")
    @ApiOperation("添加字典")
    @PostMapping()
    public JsonResult add(@RequestBody Dictionary dictionary) {
        if (dictionaryService.count(new QueryWrapper<Dictionary>().eq("dict_code", dictionary.getDictCode())) > 0) {
            return JsonResult.error("字典标识已存在");
        }
        if (dictionaryService.count(new QueryWrapper<Dictionary>().eq("dict_name", dictionary.getDictName())) > 0) {
            return JsonResult.error("字典名称已存在");
        }
        if (dictionaryService.save(dictionary)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @PreAuthorize("hasAuthority('sys:dict:update')")
    @ApiOperation("修改字典")
    @PutMapping()
    public JsonResult update(@RequestBody Dictionary dictionary) {
        if (dictionaryService.count(new QueryWrapper<Dictionary>().eq("dict_code", dictionary.getDictCode())
                .ne("dict_id", dictionary.getDictId())) > 0) {
            return JsonResult.error("字典标识已存在");
        }
        if (dictionaryService.count(new QueryWrapper<Dictionary>().eq("dict_name", dictionary.getDictName())
                .ne("dict_id", dictionary.getDictId())) > 0) {
            return JsonResult.error("字典名称已存在");
        }
        if (dictionaryService.updateById(dictionary)) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @PreAuthorize("hasAuthority('sys:dict:remove')")
    @ApiOperation("删除字典")
    @DeleteMapping("/{id}")
    public JsonResult remove(@PathVariable("id") Integer id) {
        if (dictionaryService.removeById(id)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

    @PreAuthorize("hasAuthority('sys:dict:save')")
    @ApiOperation("批量添加字典")
    @PostMapping("/batch")
    public JsonResult saveBatch(@RequestBody List<Dictionary> list) {
        // 对集合本身进行非空和重复校验
        StringBuilder sb = new StringBuilder();
        sb.append(CoreUtil.listCheckBlank(list, "dictCode", "字典标识"));
        sb.append(CoreUtil.listCheckBlank(list, "dictName", "字典名称"));
        sb.append(CoreUtil.listCheckRepeat(list, "dictCode", "字典标识"));
        sb.append(CoreUtil.listCheckRepeat(list, "dictName", "字典名称"));
        if (sb.length() != 0) return JsonResult.error(sb.toString());
        // 数据库层面校验
        if (dictionaryService.count(new QueryWrapper<Dictionary>().in("dict_code",
                list.stream().map(Dictionary::getDictCode).collect(Collectors.toList()))) > 0) {
            return JsonResult.error("字典标识已存在");
        }
        if (dictionaryService.saveBatch(list)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @PreAuthorize("hasAuthority('sys:dict:remove')")
    @ApiOperation("批量删除字典")
    @DeleteMapping("/batch")
    public JsonResult removeBatch(@RequestBody List<Integer> ids) {
        if (dictionaryService.removeByIds(ids)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

}
