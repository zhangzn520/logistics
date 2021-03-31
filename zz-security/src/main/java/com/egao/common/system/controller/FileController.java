package com.egao.common.system.controller;

import cn.hutool.core.util.StrUtil;
import com.egao.common.core.annotation.OperLog;
import com.egao.common.core.utils.FileServerUtil;
import com.egao.common.core.web.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfan on 2018-12-24 16:10
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Value("${config.upload-location:0}")
    private Integer uploadLocation;  // 文件上传磁盘位置
    @Value("${config.upload-uuid-name:false}")
    private Boolean uploadUuidName;  // 文件上传是否使用uuid命名
    @Value("${config.thumbnail-size:60}")
    private Integer thumbnailSize;  // 生成缩略图的大小
    @Value("${config.open-office-home:}")
    private String openOfficeHome;  // OpenOffice的安装目录

    @PreAuthorize("hasAuthority('sys:file:upload')")
    @OperLog(value = "文件管理", desc = "上传文件", param = false, result = true)
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public JsonResult upload(@RequestParam MultipartFile file, HttpServletRequest request) {
        try {
            File upload = FileServerUtil.upload(file, getUploadDir(), uploadUuidName);
            return getUploadResult(upload, file.getOriginalFilename(), request);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error("上传失败").put("error", e.toString());
        }
    }

    @PreAuthorize("hasAuthority('sys:file:upload')")
    @OperLog(value = "文件管理", desc = "上传base64文件", param = false, result = true)
    @ApiOperation("上传base64文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base64", value = "base64", required = true, dataType = "string")
    })
    @PostMapping("/upload/base64")
    public JsonResult uploadBase64(String base64, String fileName, HttpServletRequest request) {
        try {
            File upload = FileServerUtil.upload(base64, fileName, getUploadDir());
            return getUploadResult(upload, fileName, request);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error("上传失败").put("error", e.toString());
        }
    }

    /* 封装上传成功的返回结果 */
    private JsonResult getUploadResult(File file, String fileName, HttpServletRequest request) {
        String url = file.getAbsolutePath().substring(getUploadDir().length() - 1).replace("\\", "/");
        String requestURL = StrUtil.removeSuffix(request.getRequestURL(), "upload/base64");
        requestURL = StrUtil.removeSuffix(requestURL, "upload");
        return JsonResult.ok("上传成功")
                .put("url", url).put("location", requestURL + url)
                .put("fileName", StrUtil.isBlank(fileName) ? file.getName() : fileName)
                .put("dir", "/" + StrUtil.removeSuffix(file.getParentFile().getName(), "/"));
    }

    @ApiOperation("下载文件")
    @GetMapping("/download/{dir}/{name:.+}")
    public void download(@PathVariable("dir") String dir, @PathVariable("name") String name,
                         HttpServletResponse response, HttpServletRequest request) {
        FileServerUtil.preview(new File(getUploadDir(), dir + "/" + name), true, null, getPdfOutDir(), response, request);
    }

    @ApiOperation("查看缩略图")
    @GetMapping("/thumbnail/{dir}/{name:.+}")
    public void thumbnail(@PathVariable("dir") String dir, @PathVariable("name") String name,
                          HttpServletResponse response, HttpServletRequest request) {
        FileServerUtil.previewThumbnail(new File(getUploadDir(), dir + "/" + name),
                new File(getUploadSmDir(), dir + "/" + name), thumbnailSize, openOfficeHome, response, request);
    }

    @ApiOperation("预览原文件")
    @GetMapping("/{dir}/{name:.+}")
    public void preview(@PathVariable("dir") String dir, @PathVariable("name") String name,
                        HttpServletResponse response, HttpServletRequest request) {
        FileServerUtil.preview(new File(getUploadDir(), dir + "/" + name), getPdfOutDir(), openOfficeHome, response, request);
    }

    @PreAuthorize("hasAuthority('sys:file:remove')")
    @OperLog(value = "文件管理", desc = "删除文件", result = true)
    @ApiOperation("删除文件")
    @DeleteMapping("/remove")
    public JsonResult remove(String path) {
        if (path != null && !path.trim().isEmpty()) {
            File file = new File(getUploadDir(), path);
            if (file.delete()) new File(getUploadSmDir(), path).delete();
        }
        return JsonResult.ok("删除成功");
    }

    @PreAuthorize("hasAuthority('sys:file:list')")
    @OperLog(value = "文件管理", desc = "查询全部")
    @ApiOperation("查询全部文件")
    @GetMapping("/list")
    public JsonResult list(String directory, String sort, String order) {
        if (directory == null || directory.equals("/")) directory = "";
        File file = new File(getUploadDir(), directory);
        if (!directory.isEmpty() && !directory.endsWith("/")) directory = directory + "/";
        List<Map<String, Object>> list = FileServerUtil.list(file, "file/" + directory, "file/thumbnail/" + directory);
        // 设置默认排序规则
        if (sort == null || sort.trim().isEmpty()) {
            sort = "updateTime";
            if (order == null || order.trim().isEmpty()) order = "desc";
        }
        // 根据传递的参数排序
        String finalSort = sort, finalOrder = order;
        if ("length".equals(sort) || "updateTime".equals(sort)) {
            list.sort((o1, o2) -> {
                if ("desc".equals(finalOrder)) {
                    return ((Long) o2.get(finalSort)).compareTo((Long) o1.get(finalSort));
                } else {
                    return ((Long) o1.get(finalSort)).compareTo((Long) o2.get(finalSort));
                }
            });
        } else if ("name".equals(sort)) {
            list.sort((o1, o2) -> {
                if ("desc".equals(finalOrder)) {
                    return ((String) o2.get(finalSort)).compareTo((String) o1.get(finalSort));
                } else {
                    return ((String) o1.get(finalSort)).compareTo((String) o2.get(finalSort));
                }
            });
        }
        // 把文件夹排在前面
        list.sort((o1, o2) -> ((Boolean) o2.get("isDirectory")).compareTo((Boolean) o1.get("isDirectory")));
        return JsonResult.ok().setData(list);
    }

    /* 文件存放位置 */
    private String getBaseDir() {
        return File.listRoots()[uploadLocation] + "/upload/";
    }

    /* 文件上传目录位置 */
    private String getUploadDir() {
        return getBaseDir() + "file/";
    }

    /* 缩略图存放位置 */
    private String getUploadSmDir() {
        return getBaseDir() + "thumbnail/";
    }

    /* office预览生成pdf缓存位置 */
    private String getPdfOutDir() {
        return getBaseDir() + "pdf/";
    }

}
