package com.egao.generator.expand;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 项目生成模板引擎
 * Created by wangfan on 2020-01-16 00:49
 */
public class GenTemplateEngine extends AbstractTemplateEngine {
    private GroupTemplate groupTemplate;
    /**
     * 解压后的模板所存放的位置
     */
    private String tplDir;
    /**
     * 模板名称
     */
    private String tplName;
    /**
     * 模板配置
     */
    private TplConfig tplConfig;

    public GenTemplateEngine(String tplDir, String tplName) {
        this.tplDir = tplDir;
        this.tplName = tplName;
    }

    public String getTplDir() {
        return tplDir;
    }

    public void setTplDir(String tplDir) {
        this.tplDir = tplDir;
    }

    public String getTplName() {
        return tplName;
    }

    public void setTplName(String tplName) {
        this.tplName = tplName;
    }

    public void setTplConfig(TplConfig tplConfig) {
        this.tplConfig = tplConfig;
    }

    public TplConfig getTplConfig() {
        if (tplConfig == null) {
            try {
                String content = new FileReader(tplDir + tplName + "/config.json").readString();
                tplConfig = jsonParseObject(content, TplConfig.class);
                setTplConfig(tplConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tplConfig == null) {
                throw new RuntimeException("模板" + tplName + "的配置文件读取失败");
            }
        }
        return tplConfig;
    }

    @Override
    public AbstractTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        try {
            Configuration cfg = Configuration.defaultConfiguration();
            FileResourceLoader loader = new FileResourceLoader(tplDir + tplName);
            groupTemplate = new GroupTemplate(loader, cfg);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return this;
    }

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
        FileOutputStream fileOutputStream = null;
        try {
            Template template = groupTemplate.getTemplate(templatePath);
            fileOutputStream = new FileOutputStream(outputFile);
            template.binding(objectMap);
            template.renderTo(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(fileOutputStream);
        }
    }

    /**
     * 获取crud模板路径
     */
    @Override
    public String templateFilePath(String filePath) {
        return StrUtil.removePrefix(filePath, "/templates/") + ".btl";
    }

    /**
     * 是否生成页面
     */
    public boolean isGenPage() {
        TplConfig tc = getTplConfig();
        return !(tc == null || tc.getPages() == null || tc.getPages().size() == 0);
    }

    /**
     * 获取页面模板配置
     *
     * @param srcDir    src目录
     * @param modelName 模块名
     * @return List<FileOutConfig>
     */
    public List<FileOutConfig> getFocList(String srcDir, String modelName) {
        List<FileOutConfig> focList = new ArrayList<>();
        if (isGenPage()) {
            for (TplPage page : tplConfig.getPages()) {
                focList.add(new FileOutConfig(page.getTpl()) {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        String suffix = page.getTpl().substring(4, page.getTpl().lastIndexOf("."));
                        String mName = StrUtil.isBlank(modelName) ? "" : modelName + "/";
                        String nameSuffix = page.getNameSuffix() == null ? "" : page.getNameSuffix();
                        return srcDir + page.getOutput() + mName + tableInfo.getEntityPath() + nameSuffix + suffix;
                    }
                });
            }
        }
        return focList;
    }

    /**
     * 解析json
     */
    public <T> T jsonParseObject(String json, Class<T> clazz) {
        if (json != null && !json.trim().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readValue(json, clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
