package com.egao.generator.expand;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 项目生成工具类
 * Created by wangfan on 2020-01-16 00:49
 */
public class GenUtil {
    private String baseDir;  // 项目缓存位置

    // 项目模板路径
    public String getBaseDir() {
        return this.baseDir;
    }

    // 项目模板路径
    public String getTplDir() {
        return this.baseDir + "tpl/";
    }

    // 项目模板解压后的路径
    public String getTemplateDir() {
        return this.baseDir + "template/";
    }

    // 项目生成路径
    public String getTempDir() {
        return this.baseDir + "temp/";
    }

    // 项目生成完打包输出路径
    public String getOutputDir() {
        return this.baseDir + "output/";
    }

    public GenUtil(int index) {
        this.baseDir = File.listRoots()[index] + "/easyweb-generator/";
    }

    /**
     * 查询数据库所有表信息
     *
     * @param dbUrl        数据库连接地址
     * @param dbUsername   数据库账号
     * @param dbPassword   数据库密码
     * @param dbDriverName 数据库连接驱动名
     * @return List<TableInfo>
     */
    public static List<TableInfo> listTableInfo(String dbUrl, String dbUsername, String dbPassword, String dbDriverName) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(dbUrl);
        dsc.setUsername(dbUsername);
        dsc.setPassword(dbPassword);
        dsc.setDriverName(dbDriverName);
        ConfigBuilder configBuilder = new ConfigBuilder(null, dsc, null, null, null);
        return configBuilder.getTableInfoList();
    }

    /**
     * 生成项目
     *
     * @param genConfig 项目生成配置
     * @return 生成后的压缩包输出路径
     */
    public String gen(GenConfig genConfig) {
        GenTemplateEngine templateEngine = new GenTemplateEngine(getTemplateDir(), genConfig.getTplName());
        String projectId = IdUtil.objectId();
        String projectDir = getTempDir() + projectId + "/" + genConfig.getProjectName() + "/";
        // 生成项目框架
        genProject(projectDir, templateEngine);
        // 生成crud
        genCrud(projectDir + "src/main/", genConfig, templateEngine);
        // 自定义替换内容
        doReplaces(projectDir, genConfig, templateEngine);
        // 修改框架包名
        updatePackages(projectDir, genConfig, templateEngine);
        // 不生成页面移除框架的页面
        if (!templateEngine.isGenPage()) {
            FileUtil.del(new File(projectDir, "src/main/resources/static"));
        }
        // 打包项目
        String outputPath = projectId + "/" + genConfig.getProjectName() + ".zip";
        ZipUtil.zip(getTempDir() + projectId + "/", getOutputDir() + outputPath);
        return outputPath;
    }

    /**
     * 获取生成后的压缩包文件
     *
     * @param path 文件路径
     * @return File
     */
    public File getOutputFile(String path) {
        return new File(getOutputDir(), path);
    }

    /**
     * 获取模板列表
     */
    public List<Map<String, Object>> listTpl() {
        List<Map<String, Object>> list = new ArrayList<>();
        File dir = new File(getTplDir());
        if (!dir.exists() && dir.mkdirs()) return list;
        File[] files = dir.listFiles();
        if (files == null) return list;
        for (File f : files) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", StrUtil.removeSuffix(f.getName(), ".zip"));
            map.put("size", f.length());
            list.add(map);
        }
        return list;
    }

    /**
     * 上传模板
     */
    public boolean upload(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) return false;
        File f = new File(getTplDir(), name);
        if (!f.getParentFile().exists() && !f.getParentFile().mkdirs()) return false;
        try {
            file.transferTo(f);
            FileUtil.del(new File(getTemplateDir(), name));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取历史生成记录
     */
    public List<Map<String, Object>> history() {
        List<Map<String, Object>> list = new ArrayList<>();
        File dir = new File(getOutputDir());
        if (!dir.exists() && !dir.mkdirs()) return list;
        File[] files = dir.listFiles();
        if (files == null) return list;
        for (File file : files) {
            File[] fs = file.listFiles();
            if (fs != null && fs.length > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", file.getName());
                map.put("name", StrUtil.removeSuffix(fs[0].getName(), ".zip"));
                map.put("updateTime", fs[0].lastModified());  // 最后修改时间
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 生成项目框架
     *
     * @param projectDir     项目生成位置
     * @param templateEngine GenTemplateEngine
     */
    private void genProject(String projectDir, GenTemplateEngine templateEngine) {
        // 初次使用解压模板
        File tplFile = new File(getTemplateDir(), templateEngine.getTplName());
        File[] listFiles = tplFile.listFiles();
        if (!tplFile.exists() || listFiles == null || listFiles.length == 0) {
            ZipUtil.unzip(new File(getTplDir(), templateEngine.getTplName() + ".zip"), tplFile, Charset.forName("UTF-8"));
        }
        // 复制模板到项目生成位置中
        FileUtil.copyContent(new File(tplFile, "/project"), new File(projectDir), true);
    }

    /**
     * 生成crud
     *
     * @param srcDir         src目录
     * @param genConfig      项目生成配置
     * @param templateEngine 模板引擎
     */
    private static void genCrud(String srcDir, GenConfig genConfig, GenTemplateEngine templateEngine) {
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setAuthor(genConfig.getAuthor());
        gc.setActiveRecord(false);  // 是否是ActiveRecord模式
        gc.setEnableCache(false);  // 是否开启二级缓存
        gc.setServiceName("%sService");  // service命名规范
        gc.setOutputDir(srcDir + "java/");  // 输出位置
        gc.setFileOverride(true);
        gc.setOpen(false);
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(genConfig.getDbUrl());
        dsc.setUsername(genConfig.getDbUserName());
        dsc.setPassword(genConfig.getDbPassword());
        dsc.setDriverName(genConfig.getDbDriverName());
        // 遍历模块生成
        for (GenModel genModel : genConfig.getModels()) {
            // 包配置
            PackageConfig pc = new PackageConfig();
            String mName = StrUtil.isBlank(genModel.getModelName()) ? "" : "." + genModel.getModelName();
            pc.setParent(genConfig.getPackageName() + mName);  // 包名
            pc.setController("controller");
            pc.setEntity("entity");
            pc.setMapper("mapper");
            pc.setXml("mapper.xml");
            pc.setService("service");
            pc.setServiceImpl("service.impl");
            // 策略配置
            StrategyConfig strategy = new StrategyConfig();
            strategy.setCapitalMode(true);  // 大写命名
            strategy.setColumnNaming(NamingStrategy.underline_to_camel); // 字段映射策略
            strategy.setNaming(NamingStrategy.underline_to_camel);  // 表名映射策略
            strategy.setLogicDeleteFieldName("deleted");  // 逻辑删除字段名
            strategy.setInclude(genModel.getTablesArray());  // 需要生成的表
            strategy.setTablePrefix(genModel.getPrefixArray());  // 需要去掉的表前缀
            // 生成代码
            AutoGenerator mpg = new AutoGenerator();
            mpg.setGlobalConfig(gc);
            mpg.setDataSource(dsc);
            mpg.setPackageInfo(pc);
            mpg.setStrategy(strategy);
            mpg.setTemplateEngine(templateEngine);  // 模板引擎
            if (StrUtil.isBlank(genModel.getModelName())) {
                genModel.setModelName(genConfig.getPackageName().substring(genConfig.getPackageName().lastIndexOf(".") + 1));
            }
            // 增加模板数据
            InjectionConfig ic = new InjectionConfig() {
                @Override
                public void initMap() {
                    Map<String, Object> map = new HashMap<>();
                    map.put("genConfig", genConfig);
                    map.put("genModel", genModel);
                    map.put("genDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    map.put("genModelList", genConfig.getModels());
                    setMap(map);
                }
            };
            // 增加页面生成
            List<FileOutConfig> focList = templateEngine.getFocList(srcDir, genModel.getModelName());
            // 增加sql生成
            focList.add(new FileOutConfig("generator.sql.btl") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    String sql = srcDir + "resources/sql/generator.sql";
                    if (!new File(sql).getParentFile().exists()) {
                        new File(sql).getParentFile().mkdirs();
                    }
                    if (!new File(sql).exists()) {
                        try {
                            Map<String, Object> map = new HashMap<>();
                            map.put("cfg", ic.getMap());
                            templateEngine.writer(map, "generator.sql.btl", sql);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    replaceFileStr(new TplReplace(
                            sql,
                            new TplReplaceItem("@" + tableInfo.getName() + "_path", tableInfo.getEntityPath()),
                            new TplReplaceItem("@" + tableInfo.getName() + "_name", tableInfo.getComment())
                    ));
                    return srcDir + "sql.temp";
                }
            });
            ic.setFileOutConfigList(focList);
            mpg.setCfg(ic);
            mpg.execute();
        }
        // 删除sql临时文件
        FileUtil.del(new File(srcDir, "sql.temp"));
        // 修改日期类型
        updateDateType(new File(gc.getOutputDir()));
    }

    /**
     * 替换自定义修改内容
     *
     * @param projectDir     项目位置
     * @param genConfig      项目生成配置
     * @param templateEngine 模板引擎
     */
    private static void doReplaces(String projectDir, GenConfig genConfig, GenTemplateEngine templateEngine) {
        TplConfig tplConfig = templateEngine.getTplConfig();
        // 对外提供的数据
        Map<String, Object> tplData = new HashMap<>();
        tplData.put("projectName", genConfig.getProjectName());
        tplData.put("groupId", genConfig.getGroupId());
        tplData.put("groupIdPath", genConfig.getGroupId().replace(".", "/"));
        tplData.put("packageName", genConfig.getPackageName());
        tplData.put("packageNamePath", genConfig.getPackageName().replace(".", "/"));
        tplData.put("author", genConfig.getAuthor());
        tplData.put("dbUrl", genConfig.getDbUrl());
        tplData.put("dbUserName", genConfig.getDbUserName());
        tplData.put("dbPassword", genConfig.getDbPassword());
        tplData.put("dbDriverName", genConfig.getDbDriverName());
        /*if ("com.mysql.jdbc.Driver".equals(genConfig.getDbDriverName())) {
            tplData.put("dbDriver", "com.mysql.cj.jdbc.Driver");
            String dbUrl = genConfig.getDbUrl();
            dbUrl = dbUrl.substring(0, dbUrl.indexOf("?")) + "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
            tplData.put("dbUrl", dbUrl);
        } else {
        }*/
        List<TplReplace> replaces = tplConfig.getReplaces();
        for (TplReplace replace : replaces) {
            if (replace.getFiles().length == 0 || replace.getItems().size() == 0) continue;
            String[] files = new String[replace.getFiles().length];
            for (int i = 0; i < files.length; i++) {
                files[i] = projectDir + replace.getFiles()[i];
            }
            replace.setFiles(files);
            for (TplReplaceItem item : replace.getItems()) {
                item.setNewStr(replaceEL(item.getNewStr(), tplData));
            }
            replaceFileStr(replace);
        }
    }

    /**
     * 修改项目包名
     *
     * @param projectDir     项目位置
     * @param genConfig      项目生成配置
     * @param templateEngine 模板引擎
     */
    private static void updatePackages(String projectDir, GenConfig genConfig, GenTemplateEngine templateEngine) {
        String oldPackage = templateEngine.getTplConfig().getPackageName();
        String newPackage = genConfig.getGroupId();
        if (oldPackage.equals(newPackage)) return;
        File oldSrc = new File(projectDir, "src/main/java/" + oldPackage.replace(".", "/"));
        File newSrc = new File(projectDir, "src/main/java/" + newPackage.replace(".", "/"));
        File[] oldFiles = oldSrc.listFiles();
        if (oldFiles == null) return;
        for (File oldFile : oldFiles) {
            FileUtil.move(oldFile, newSrc, true);  // 移动文件
        }
        FileUtil.del(oldSrc);  // 删除旧目录
        updateCodePackages(newSrc, oldPackage, newPackage, null);  // 修改代码包名
    }

    /**
     * 解析el表达式
     */
    private static String replaceEL(String content, Map<String, Object> data) {
        for (String key : data.keySet()) {
            Object value = data.get(key);
            content = content.replace("${" + key + "}", value == null ? "" : String.valueOf(value));
        }
        return content;
    }

    /**
     * 替换文件内容
     */
    private static void replaceFileStr(TplReplace replace) {
        for (String file : replace.getFiles()) {
            FileOutputStream out = null;
            try {
                String content = new FileReader(file).readString();
                if (content == null || content.trim().isEmpty()) continue;
                for (TplReplaceItem item : replace.getItems()) {
                    content = content.replace(item.getOrgStr(), item.getNewStr());
                }
                out = new FileOutputStream(new File(file));
                out.write(content.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtil.close(out);
            }
        }
    }

    /**
     * 修改日期类型
     */
    private static void updateDateType(File src) {
        File[] files = src.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                updateDateType(file);
                continue;
            }
            replaceFileStr(new TplReplace(
                    file.getAbsolutePath(),
                    new TplReplaceItem("import java.time.LocalDateTime;", "import java.util.Date;"),
                    new TplReplaceItem("private LocalDateTime ", "private Date "),
                    new TplReplaceItem("public LocalDateTime ", "public Date "),
                    new TplReplaceItem("(LocalDateTime ", "(Date ")
            ));
        }
    }

    /**
     * 修改某个目录下代码的包名
     *
     * @param src        目录
     * @param oldPackage 原始包名
     * @param newPackage 新的包名
     */
    private static void updateCodePackages(File src, String oldPackage, String newPackage, List<String> child) {
        File[] files = src.listFiles();
        if (files == null) return;
        if (child == null) {
            child = new ArrayList<>();
            for (File file : files) {
                int index = file.getName().lastIndexOf(".");
                if (index == -1) {
                    child.add(file.getName());
                } else {
                    child.add(file.getName().substring(0, index));
                }
            }
        }
        for (File file : files) {
            if (file.isDirectory()) {
                updateCodePackages(file, oldPackage, newPackage, child);
                continue;
            }
            List<TplReplaceItem> items = new ArrayList<>();
            items.add(new TplReplaceItem("package " + oldPackage, "package " + newPackage));
            items.add(new TplReplaceItem("namespace=\"" + oldPackage, "namespace=\"" + newPackage));
            items.add(new TplReplaceItem("resultType=\"" + oldPackage, "resultType=\"" + newPackage));
            items.add(new TplReplaceItem("@annotation(" + oldPackage, "@annotation(" + newPackage));
            for (String name : child) {
                items.add(new TplReplaceItem("import " + oldPackage + "." + name, "import " + newPackage + "." + name));
            }
            replaceFileStr(new TplReplace(file.getAbsolutePath(), items.toArray(new TplReplaceItem[0])));
        }
    }

}
