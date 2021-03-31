package com.egao.common.system.controller;

import com.egao.common.core.annotation.OperLog;
import com.egao.common.core.web.JsonResult;
import com.egao.common.system.service.EmailService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

/**
 * Created by wangfan on 2020-03-21 0:37
 */
@Api(tags = "邮件功能")
@RestController
@RequestMapping("/api/sys/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @OperLog(value = "邮件功能", desc = "发送邮件", result = true, param = false)
    @PreAuthorize("hasAuthority('sys:email:send')")
    @PostMapping("/send")
    public JsonResult send(String title, String html, String email) {
        try {
            emailService.sendFullTextEmail(title, html, new String[]{email});
            return JsonResult.ok("发送成功");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return JsonResult.error("发送失败");
    }

}
