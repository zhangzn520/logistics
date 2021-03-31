package com.egao.generator.controller;

import com.egao.generator.expand.GenUtil;
import com.egao.generator.expand.GenConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目生成器
 * Created by wangfan on 2020-01-16 00:49
 */
@RequestMapping("/api/generator")
@RestController()
public class GeneratorController {
    @Value("${gen.cache}")
    private Integer cache;

    /**
     * 生成项目
     */
    @PostMapping()
    public Map<String, Object> generator(@RequestBody GenConfig genConfig) {
        try {
            String path = new GenUtil(cache).gen(genConfig);
            if (path == null) return error("生成失败，请检查配置");
            return ok("项目生成成功", path);
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    /**
     * 获取模板列表
     */
    @GetMapping("/templates")
    public Map<String, Object> listTpl() {
        return ok("查询成功", new GenUtil(cache).listTpl());
    }

    /**
     * 获取数据库表信息
     */
    @GetMapping("/tables")
    public Map<String, Object> listTable(String dbUrl, String dbUserName, String dbPassword, String dbDriverName) {
        return ok("查询成功", GenUtil.listTableInfo(dbUrl, dbUserName, dbPassword, dbDriverName));
    }

    /**
     * 上传模板
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(MultipartFile file) {
        if (new GenUtil(cache).upload(file)) {
            return ok("上传成功", file.getOriginalFilename());
        }
        return error("上传失败");
    }

    /**
     * 历史生成记录
     */
    @GetMapping("/history")
    public Map<String, Object> history() {
        return ok("查询成功", new GenUtil(cache).history());
    }

    /**
     * 下载生成后的压缩包
     */
    @GetMapping("/download")
    public void download(String file, HttpServletResponse response) {
        File outFile = new GenUtil(cache).getOutputFile(file);
        if (!outFile.exists()) return;
        // 设置下载文件header
        response.setContentType("application/force-download");
        String fileName = outFile.getName();
        try {
            fileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        // 输出文件流
        OutputStream os = null;
        BufferedInputStream is = null;
        try {
            os = response.getOutputStream();
            is = new BufferedInputStream(new FileInputStream(outFile));
            byte[] bytes = new byte[1024 * 256];
            int len;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 返回结果信息
     */
    private Map<String, Object> ok(String msg, Object data) {
        return result(0, msg, data);
    }

    private Map<String, Object> error(String msg) {
        return result(1, msg, null);
    }

    private Map<String, Object> result(Integer code, String msg, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", msg);
        map.put("data", data);
        return map;
    }

}
