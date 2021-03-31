package com.egao.common.system.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.egao.common.core.annotation.ApiPageParam;
import com.egao.common.core.annotation.OperLog;
import com.egao.common.core.web.*;
import com.egao.common.core.utils.CoreUtil;
import com.egao.common.system.entity.DictionaryData;
import com.egao.common.system.entity.Organization;
import com.egao.common.system.entity.Role;
import com.egao.common.system.entity.User;
import com.egao.common.system.service.DictionaryDataService;
import com.egao.common.system.service.OrganizationService;
import com.egao.common.system.service.RoleService;
import com.egao.common.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangfan on 2018-12-24 16:10
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/sys/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private OrganizationService organizationService;

    @PreAuthorize("hasAuthority('sys:user:list')")
    @OperLog(value = "用户管理", desc = "分页查询")
    @ApiOperation("分页查询用户")
    @ApiPageParam
    @GetMapping("/page")
    public PageResult<User> page(HttpServletRequest request) {
        PageParam<User> pageParam = new PageParam<>(request);
        pageParam.setDefaultOrder(null, new String[]{"create_time"});
        return new PageResult<>(userService.listPage(pageParam), pageParam.getTotal());
    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @OperLog(value = "用户管理", desc = "查询全部")
    @ApiOperation("查询全部用户")
    @GetMapping()
    public JsonResult list(HttpServletRequest request) {
        PageParam<User> pageParam = new PageParam<>(request);
        List<User> records = userService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.sortRecords(records));
    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @OperLog(value = "用户管理", desc = "根据id查询")
    @ApiOperation("根据id查询用户")
    @GetMapping("/{id}")
    public JsonResult get(@PathVariable("id") Integer id) {
        PageParam<User> pageParam = new PageParam<>();
        pageParam.put("userId", id);
        List<User> records = userService.listAll(pageParam.getNoPageParam());
        return JsonResult.ok().setData(pageParam.getOne(records));
    }

    @PreAuthorize("hasAuthority('sys:user:save')")
    @OperLog(value = "用户管理", desc = "添加", param = false, result = true)
    @ApiOperation("添加用户")
    @PostMapping()
    public JsonResult add(@RequestBody User user) {
        user.setState(0);
        user.setPassword(userService.encodePsw(user.getPassword()));
        if (userService.saveUser(user)) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @OperLog(value = "用户管理", desc = "修改", param = false, result = true)
    @ApiOperation("修改用户")
    @PutMapping()
    public JsonResult update(@RequestBody User user) {
        user.setState(null);
        user.setPassword(userService.encodePsw(user.getPassword()));
        if (userService.updateUser(user)) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @PreAuthorize("hasAuthority('sys:user:remove')")
    @OperLog(value = "用户管理", desc = "删除", result = true)
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public JsonResult remove(@PathVariable("id") Integer id) {
        if (userService.removeById(id)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @OperLog(value = "用户管理", desc = "批量修改", param = false, result = true)
    @ApiOperation("批量修改用户")
    @PutMapping("/batch")
    public JsonResult removeBatch(@RequestBody BatchParam<User> batchParam) {
        if (batchParam.update(userService, "user_id")) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @PreAuthorize("hasAuthority('sys:user:remove')")
    @OperLog(value = "用户管理", desc = "批量删除", result = true)
    @ApiOperation("批量删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "id数组", required = true, dataType = "string")
    })
    @DeleteMapping("/batch")
    public JsonResult deleteBatch(@RequestBody List<Integer> ids) {
        if (userService.removeByIds(ids)) {
            return JsonResult.ok("删除成功");
        }
        return JsonResult.error("删除失败");
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @OperLog(value = "用户管理", desc = "修改状态", result = true)
    @ApiOperation("修改用户状态")
    @PutMapping("/state/{id}")
    public JsonResult updateState(@PathVariable("id") Integer id, Integer state) {
        if (state == null || (state != 0 && state != 1)) {
            return JsonResult.error("状态值不正确");
        }
        User user = new User();
        user.setUserId(id);
        user.setState(state);
        if (userService.updateById(user)) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @OperLog(value = "用户管理", desc = "批量修改状态", result = true)
    @ApiOperation("批量修改用户状态")
    @PutMapping("/state/batch")
    public JsonResult updateStateBatch(@RequestBody BatchParam<User> batchParam) {
        User user = new User();
        user.setState(batchParam.getData().getState());
        if (user.getState() == null || (user.getState() != 0 && user.getState() != 1)) {
            return JsonResult.error("状态值不正确");
        }
        if (batchParam.update(userService, "user_id")) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @OperLog(value = "用户管理", desc = "重置密码", param = false, result = true)
    @ApiOperation("重置密码")
    @PutMapping("/psw/{id}")
    public JsonResult resetPsw(@PathVariable("id") Integer id, String password) {
        User user = new User();
        user.setUserId(id);
        user.setPassword(userService.encodePsw(password));
        if (userService.updateById(user)) {
            return JsonResult.ok("重置成功");
        } else {
            return JsonResult.error("重置失败");
        }
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @OperLog(value = "用户管理", desc = "批量重置密码", param = false, result = true)
    @ApiOperation("批量重置密码")
    @PutMapping("/psw/batch")
    public JsonResult resetPswBatch(@RequestBody BatchParam<User> batchParam) {
        User user = new User();
        user.setPassword(userService.encodePsw(batchParam.getData().getPassword()));
        if (batchParam.update(userService, "user_id")) {
            return JsonResult.ok("重置成功");
        } else {
            return JsonResult.error("重置失败");
        }
    }

    /**
     * excel导入用户
     */
    @PreAuthorize("hasAuthority('sys:user:save')")
    @OperLog(value = "用户管理", desc = "excel导入", param = false, result = true)
    @ApiOperation("excel导入用户")
    @Transactional
    @PostMapping("/import")
    public JsonResult importBatch(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        try {
            // 读取excel
            int startRow = 1;
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream(), 0);
            List<List<Object>> list = reader.read(startRow);
            // 进行非空和重复检查
            sb.append(CoreUtil.excelCheckBlank(list, startRow, 0, 1, 2, 3, 4, 7));
            sb.append(CoreUtil.excelCheckRepeat(list, startRow, 0, 5, 6));
            if (!sb.toString().isEmpty()) return JsonResult.error(sb.toString());
            // 进行数据库层面检查
            List<User> users = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                List<Object> objects = list.get(i);
                String username = String.valueOf(objects.get(0));  // 账号
                String password = String.valueOf(objects.get(1));  // 密码
                String nickname = String.valueOf(objects.get(2));  // 用户名
                String sexName = String.valueOf(objects.get(3));  // 性别
                String roleName = String.valueOf(objects.get(4));  // 角色名
                String phone = String.valueOf(objects.get(5));  // 手机号
                String email = String.valueOf(objects.get(6));  // 邮箱
                String orgName = String.valueOf(objects.get(7));  // 组织机构
                if (userService.count(new QueryWrapper<User>().eq("username", username)) > 0) {
                    sb.append("第");
                    sb.append(i + startRow + 1);
                    sb.append("行第1");
                    sb.append("列账号已存在;\r\n");
                }
                if (StrUtil.isNotBlank(phone) && userService.count(new QueryWrapper<User>().eq("phone", phone)) > 0) {
                    sb.append("第");
                    sb.append(i + startRow + 1);
                    sb.append("行第6");
                    sb.append("列手机号已存在;\r\n");
                }
                if (StrUtil.isNotBlank(email) && userService.count(new QueryWrapper<User>().eq("email", email)) > 0) {
                    sb.append("第");
                    sb.append(i + startRow + 1);
                    sb.append("行第7");
                    sb.append("列邮箱已存在;\r\n");
                }
                User user = new User();
                user.setUsername(username);
                user.setNickname(nickname);
                user.setPassword(userService.encodePsw(password));
                user.setState(0);
                user.setPhone(phone);
                user.setEmail(email);
                DictionaryData sexDictData = dictionaryDataService.listByDictCodeAndName("sex", sexName);
                if (sexDictData == null) {
                    sb.append("第");
                    sb.append(i + startRow + 1);
                    sb.append("行第4");
                    sb.append("列性别不存在;\r\n");
                } else {
                    user.setSex(sexDictData.getDictDataId());
                }
                Role role = roleService.getOne(new QueryWrapper<Role>().eq("role_name", roleName), false);
                if (role == null) {
                    sb.append("第");
                    sb.append(i + startRow + 1);
                    sb.append("行第5");
                    sb.append("列角色不存在;\r\n");
                } else {
                    user.setRoleIds(Collections.singletonList(role.getRoleId()));
                }
                Organization org = organizationService.getOne(new QueryWrapper<Organization>().eq("organization_full_name", orgName), false);
                if (org == null) {
                    sb.append("第");
                    sb.append(i + startRow + 1);
                    sb.append("行第8");
                    sb.append("列机构不存在;\r\n");
                } else {
                    user.setOrganizationId(org.getOrganizationId());
                }
                users.add(user);
            }
            if (!sb.toString().isEmpty()) return JsonResult.error(sb.toString());
            // 开始添加用户
            int okNum = 0, errorNum = 0;
            for (User user : users) {
                if (userService.saveUser(user)) okNum++;
                else errorNum++;
            }
            return JsonResult.ok("导入完成，成功" + okNum + "条，失败" + errorNum + "条");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonResult.error("导入失败");
    }

}
