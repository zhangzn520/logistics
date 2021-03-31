package com.egao.common.system.controller;

import cn.hutool.core.util.StrUtil;
import com.egao.common.core.annotation.OperLog;
import com.egao.common.core.web.BaseController;
import com.egao.common.core.web.JsonResult;
import com.egao.common.system.entity.Menu;
import com.egao.common.system.entity.User;
import com.egao.common.system.service.MenuService;
import com.egao.common.system.service.UserService;
import com.wf.captcha.SpecCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by wangfan on 2018-12-24 16:10
 */
@Api(tags = "登录认证")
@RestController
@RequestMapping("/api")
public class MainController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;

    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "账号", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "string", paramType = "query"),
    })
    @PostMapping("/login")
    public void login(String username, String password) {
        // 登录操作由JwtLoginFilter完成
    }

    @ApiOperation("获取登录用户菜单")
    @GetMapping("/main/menu")
    public JsonResult userMenu() {
        List<Menu> userMenu = menuService.getUserMenu(getLoginUserId(), Menu.TYPE_MENU);
        return JsonResult.ok().setData(menuService.toMenuTree(userMenu, 0));
    }

    @ApiOperation("获取登录用户信息")
    @GetMapping("/main/user")
    public JsonResult userInfo() {
        return JsonResult.ok().setData(userService.getFullById(getLoginUserId()));
    }

    @PreAuthorize("hasAuthority('main:user:update')")
    @OperLog(value = "登录认证", desc = "修改个人信息", param = false, result = true)
    @ApiOperation("修改个人信息")
    @PutMapping("/main/user")
    public JsonResult updateInfo(User user) {
        user.setUserId(getLoginUserId());
        // 不能修改的字段
        user.setState(null);
        user.setPassword(null);
        user.setUsername(null);
        user.setOrganizationId(null);
        if (userService.updateById(user)) {
            User loginUser = getLoginUser();
            if (user.getNickname() != null) loginUser.setNickname(user.getNickname());
            if (user.getAvatar() != null) loginUser.setAvatar(user.getAvatar());
            return JsonResult.ok("保存成功").setData(userService.getFullById(user.getUserId()));
        }
        return JsonResult.error("保存失败");
    }

    @PreAuthorize("hasAuthority('main:user:password')")
    @OperLog(value = "登录认证", desc = "修改自己密码", param = false, result = true)
    @ApiOperation("修改自己密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPsw", value = "旧密码", required = true, dataType = "string"),
            @ApiImplicitParam(name = "newPsw", value = "新密码", required = true, dataType = "string")
    })
    @PutMapping("/main/password")
    public JsonResult updatePsw(String oldPsw, String newPsw, HttpServletRequest request) {
        if (StrUtil.hasBlank(oldPsw, newPsw)) {
            return JsonResult.error("参数不能为空");
        }
        if (getLoginUserId() == null) {
            return JsonResult.error("未登录");
        }
        if (!userService.comparePsw(userService.getById(getLoginUserId()).getPassword(), oldPsw)) {
            return JsonResult.error("原密码输入不正确");
        }
        User user = new User();
        user.setUserId(getLoginUserId());
        user.setPassword(userService.encodePsw(newPsw));
        if (userService.updateById(user)) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

    @ApiOperation("图形验证码")
    @GetMapping("/file/captcha")
    public JsonResult captcha(HttpServletRequest request) {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        return JsonResult.ok().setData(specCaptcha.toBase64()).put("text", specCaptcha.text().toLowerCase());
    }

}
