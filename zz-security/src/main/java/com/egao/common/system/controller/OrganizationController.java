package com.egao.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.egao.common.core.annotation.ApiPageParam;
import com.egao.common.core.annotation.OperLog;
import com.egao.common.core.web.*;
import com.egao.common.system.entity.Organization;
import com.egao.common.system.service.OrganizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by AutoGenerator on 2020-03-14 11:29:04
 */
@Api(tags = "组织机构管理")
@RestController
@RequestMapping("/api/sys/organization")
public class OrganizationController extends BaseController {
    @Autowired
    private OrganizationService organizationService;

    @PreAuthorize("hasAuthority('sys:org:list')")
    @OperLog(value = "机构管理", desc = "分页查询")
    @ApiOperation("分页查询组织机构")
    @ApiPageParam
    @GetMapping("/page")
    public PageResult<Organization> page(HttpServletRequest request) {
        PageParam<Organization> pageParam = new PageParam<>(request);
        return new PageResult<>(organizationService.listPage(pageParam), pageParam.getTotal());
    }

    @PreAuthorize("hasAuthority('sys:org:list')")
    @OperLog(value = "机构管理", desc = "查询全部")
    @ApiOperation("查询全部组织机构")
    @GetMapping()
    public JsonResult list(HttpServletRequest request) {
        PageParam<Organization> pageParam = new PageParam<>(request);
        List<Organization> records = organizationService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.sortRecords(records));
    }

    @PreAuthorize("hasAuthority('sys:org:list')")
    @OperLog(value = "机构管理", desc = "根据id查询")
    @ApiOperation("根据id查询组织机构")
    @GetMapping("/{id}")
    public JsonResult get(@PathVariable("id") Integer id) {
        PageParam<Organization> pageParam = new PageParam<>();
        pageParam.put("organizationId", id);
        List<Organization> records = organizationService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.getOne(records));
    }

    @PreAuthorize("hasAuthority('sys:org:save')")
    @OperLog(value = "机构管理", desc = "添加", param = false, result = true)
    @ApiOperation("添加组织机构")
    @PostMapping()
    public JsonResult add(@RequestBody Organization organization) {
        if (organization.getParentId() == null) organization.setParentId(0);
        if (organizationService.count(new QueryWrapper<Organization>()
                .eq("organization_name", organization.getOrganizationName())
                .eq("parent_id", organization.getParentId())) > 0) {
            return JsonResult.error("机构名称已存在");
        }
        if (organizationService.save(organization)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @PreAuthorize("hasAuthority('sys:org:update')")
    @OperLog(value = "机构管理", desc = "修改", param = false, result = true)
    @ApiOperation("修改组织机构")
    @PutMapping()
    public JsonResult update(@RequestBody Organization organization) {
        if (organization.getOrganizationName() != null) {
            if (organization.getParentId() == null) organization.setParentId(0);
            if (organizationService.count(new QueryWrapper<Organization>()
                    .eq("organization_name", organization.getOrganizationName())
                    .eq("parent_id", organization.getParentId())
                    .ne("organization_id", organization.getOrganizationId())) > 0) {
                return JsonResult.error("机构名称已存在");
            }
        }
        if (organizationService.updateById(organization)) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @PreAuthorize("hasAuthority('sys:org:remove')")
    @OperLog(value = "机构管理", desc = "删除", result = true)
    @ApiOperation("删除组织机构")
    @DeleteMapping("/{id}")
    public JsonResult remove(@PathVariable("id") Integer id) {
        if (organizationService.removeById(id)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

    @PreAuthorize("hasAuthority('sys:org:save')")
    @OperLog(value = "机构管理", desc = "批量添加", param = false, result = true)
    @ApiOperation("批量添加组织机构")
    @PostMapping("/batch")
    public JsonResult saveBatch(@RequestBody List<Organization> organizationList) {
        if (organizationService.saveBatch(organizationList)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @PreAuthorize("hasAuthority('sys:org:update')")
    @OperLog(value = "机构管理", desc = "批量修改", result = true)
    @ApiOperation("批量修改组织机构")
    @PutMapping("/batch")
    public JsonResult updateBatch(@RequestBody BatchParam<Organization> batchParam) {
        if (batchParam.update(organizationService, "organization_id")) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @PreAuthorize("hasAuthority('sys:org:remove')")
    @OperLog(value = "机构管理", desc = "批量删除", result = true)
    @ApiOperation("批量删除组织机构")
    @DeleteMapping("/batch")
    public JsonResult removeBatch(@RequestBody List<Integer> ids) {
        if (organizationService.removeByIds(ids)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

}
