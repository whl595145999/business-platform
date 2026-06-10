package com.whl.platform.generator.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import com.whl.framework.mybatis.enums.DataBaseType;
import com.whl.platform.generator.constant.GenConstants;
import com.whl.framework.core.utils.DateUtils;
import com.whl.framework.core.utils.StringUtils;
import com.whl.framework.json.utils.JsonUtils;
import com.whl.framework.mybatis.helper.DataBaseHelper;
import com.whl.platform.generator.domain.GenTable;
import com.whl.platform.generator.domain.GenTableColumn;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.velocity.VelocityContext;

import java.util.*;

/**
 * 模板处理工具类
 *
 * @author ruoyi
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VelocityUtils {

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "main/java";

    /**
     * mybatis空间路径
     */
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /**
     * 默认上级菜单，系统工具
     */
    private static final String DEFAULT_PARENT_MENU_ID = "3";

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTable genTable) {
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String tplCategory = genTable.getTplCategory();
        String functionName = genTable.getFunctionName();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tplCategory", genTable.getTplCategory());
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("functionName", StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StringUtils.uncapitalize(genTable.getClassName()));
        velocityContext.put("moduleName", genTable.getModuleName());
        velocityContext.put("BusinessName", StringUtils.capitalize(genTable.getBusinessName()));
        velocityContext.put("businessName", genTable.getBusinessName());
        velocityContext.put("basePackage", getPackagePrefix(packageName));
        velocityContext.put("packageName", packageName);
        velocityContext.put("author", genTable.getFunctionAuthor());
        velocityContext.put("datetime", DateUtils.getDate());
        velocityContext.put("pkColumn", genTable.getPkColumn());
        velocityContext.put("importList", getImportList(genTable));
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns", genTable.getColumns());
        velocityContext.put("table", genTable);
        velocityContext.put("dicts", getDicts(genTable));
        setMenuVelocityContext(velocityContext, genTable);
        if (GenConstants.TPL_TREE.equals(tplCategory)) {
            setTreeVelocityContext(velocityContext, genTable);
        } else if (GenConstants.TPL_SCM_CRUD.equals(tplCategory)) {
            setScmVelocityContext(velocityContext, genTable);
        }
        return velocityContext;
    }

    /**
     * SCM 模板上下文。
     */
    public static void setScmVelocityContext(VelocityContext context, GenTable genTable) {
        Dict paramsObj = JsonUtils.parseMap(genTable.getOptions());
        String scmDomain = paramsObj.getStr(GenConstants.SCM_DOMAIN);
        if (StringUtils.isBlank(scmDomain)) {
            scmDomain = GenUtils.resolveScmDomain(genTable.getTableName());
        }
        String scmDomainCap = GenUtils.capitalizeDomain(scmDomain);
        String className = genTable.getClassName();
        String businessName = genTable.getBusinessName();
        String scmResource = paramsObj.getStr(GenConstants.SCM_RESOURCE);
        if (StringUtils.isBlank(scmResource)) {
            scmResource = GenUtils.pluralizeResource(businessName);
        }
        String scmPermPrefix = paramsObj.getStr(GenConstants.SCM_PERM_PREFIX);
        if (StringUtils.isBlank(scmPermPrefix)) {
            scmPermPrefix = "scm:" + scmDomain + ":" + businessName;
        }
        String scmBusinessKey = paramsObj.getStr(GenConstants.SCM_BUSINESS_KEY);
        if (StringUtils.isBlank(scmBusinessKey)) {
            scmBusinessKey = "id";
        }
        GenTableColumn businessKeyColumn = resolveBusinessKeyColumn(genTable, scmBusinessKey);
        String scmMenuId = paramsObj.getStr(GenConstants.SCM_MENU_ID);
        String scmMenuOrder = paramsObj.getStr(GenConstants.SCM_MENU_ORDER);
        if (StringUtils.isBlank(scmMenuOrder)) {
            scmMenuOrder = "0";
        }
        String scmMenuComponent = paramsObj.getStr(GenConstants.SCM_MENU_COMPONENT);
        if (StringUtils.isBlank(scmMenuComponent)) {
            scmMenuComponent = "scm/" + scmDomain + "/" + businessName + "/index";
        }
        boolean scmGenerateOptions = !Boolean.FALSE.equals(paramsObj.getBool(GenConstants.SCM_GENERATE_OPTIONS));

        context.put("scmDomain", scmDomain);
        context.put("scmDomainCap", scmDomainCap);
        context.put("scmResource", scmResource);
        context.put("scmApiPrefix", "/api/scm/" + scmDomain + "/" + scmResource);
        context.put("scmPermPrefix", scmPermPrefix);
        context.put("scmBusinessKey", scmBusinessKey);
        context.put("businessKeyColumn", businessKeyColumn);
        context.put("scmMenuId", scmMenuId);
        context.put("scmMenuOrder", scmMenuOrder);
        context.put("scmMenuComponent", scmMenuComponent);
        context.put("scmGenerateOptions", scmGenerateOptions);
        context.put("scmEntityClass", scmDomainCap + className);
        context.put("scmErrorCodeClass", scmDomainCap + "ErrorCode");
        context.put("packageScmApi", "com.whl.scm." + scmDomain + ".api");
        context.put("packageScmBiz", "com.whl.scm." + scmDomain + ".biz");
        context.put("packageScmAdapter", "com.whl.scm." + scmDomain + ".adapter.admin");
        context.put("scmGenTag", scmDomain + "-" + businessName);
        context.put("pkIncrement", genTable.getPkColumn() != null && genTable.getPkColumn().isIncrement());
    }

    private static GenTableColumn resolveBusinessKeyColumn(GenTable genTable, String businessKey) {
        for (GenTableColumn column : genTable.getColumns()) {
            if (StringUtils.equals(column.getColumnName(), businessKey)) {
                return column;
            }
        }
        return genTable.getPkColumn();
    }

    public static void setMenuVelocityContext(VelocityContext context, GenTable genTable) {
        String options = genTable.getOptions();
        Dict paramsObj = JsonUtils.parseMap(options);
        String parentMenuId = getParentMenuId(paramsObj);
        context.put("parentMenuId", parentMenuId);
    }

    public static void setTreeVelocityContext(VelocityContext context, GenTable genTable) {
        String options = genTable.getOptions();
        Dict paramsObj = JsonUtils.parseMap(options);
        String treeCode = getTreecode(paramsObj);
        String treeParentCode = getTreeParentCode(paramsObj);
        String treeName = getTreeName(paramsObj);

        context.put("treeCode", treeCode);
        context.put("treeParentCode", treeParentCode);
        context.put("treeName", treeName);
        context.put("expandColumn", getExpandColumn(genTable));
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE)) {
            context.put("tree_parent_code", paramsObj.get(GenConstants.TREE_PARENT_CODE));
        }
        if (paramsObj.containsKey(GenConstants.TREE_NAME)) {
            context.put("tree_name", paramsObj.get(GenConstants.TREE_NAME));
        }
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList(String tplCategory, String dsName) {
        if (GenConstants.TPL_SCM_CRUD.equals(tplCategory)) {
            return getScmTemplateList();
        }
        List<String> templates = new ArrayList<>();
        templates.add("vm/java/domain.java.vm");
        templates.add("vm/java/vo.java.vm");
        templates.add("vm/java/bo.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/controller.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
        DataBaseType dataBaseType = DataBaseHelper.getDataBaseType(dsName);
        if (dataBaseType.isOracle()) {
            templates.add("vm/sql/oracle/sql.vm");
        } else if (dataBaseType.isPostgreSql()) {
            templates.add("vm/sql/postgres/sql.vm");
        } else if (dataBaseType.isSqlServer()) {
            templates.add("vm/sql/sqlserver/sql.vm");
        } else {
            templates.add("vm/sql/sql.vm");
        }
        templates.add("vm/ts/api.ts.vm");
        templates.add("vm/ts/types.ts.vm");
        if (GenConstants.TPL_CRUD.equals(tplCategory)) {
            templates.add("vm/vue/index.vue.vm");
        } else if (GenConstants.TPL_TREE.equals(tplCategory)) {
            templates.add("vm/vue/index-tree.vue.vm");
        }
        return templates;
    }

    private static List<String> getScmTemplateList() {
        List<String> templates = new ArrayList<>();
        templates.add("vm/scm/ScmPageResult.java.vm");
        templates.add("vm/scm/PageQuery.java.vm");
        templates.add("vm/scm/GetQuery.java.vm");
        templates.add("vm/scm/ListOptionsQuery.java.vm");
        templates.add("vm/scm/CreateCommand.java.vm");
        templates.add("vm/scm/UpdateCommand.java.vm");
        templates.add("vm/scm/DeleteCommand.java.vm");
        templates.add("vm/scm/Dto.java.vm");
        templates.add("vm/scm/Vo.java.vm");
        templates.add("vm/scm/Facade.java.vm");
        templates.add("vm/scm/Entity.java.vm");
        templates.add("vm/scm/Mapper.java.vm");
        templates.add("vm/scm/AppService.java.vm");
        templates.add("vm/scm/ScmController.java.vm");
        templates.add("vm/scm/SaveRequest.java.vm");
        templates.add("vm/scm/scm_menu.sql.vm");
        templates.add("vm/scm/api.ts.vm");
        templates.add("vm/scm/types.snippet.vm");
        templates.add("vm/scm/api-merge.snippet.vm");
        templates.add("vm/scm/ErrorCode.snippet.vm");
        templates.add("vm/scm/index.vue.vm");
        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTable genTable) {
        if (GenConstants.TPL_SCM_CRUD.equals(genTable.getTplCategory()) && template.contains("vm/scm/")) {
            return getScmFileName(template, genTable);
        }
        String fileName = "";
        // 包路径
        String packageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getClassName();
        // 业务名称
        String businessName = genTable.getBusinessName();

        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        String mybatisPath = MYBATIS_PATH + "/" + moduleName;
        String vuePath = "vue";

        if (template.contains("domain.java.vm")) {
            fileName = StringUtils.format("{}/domain/{}.java", javaPath, className);
        }
        if (template.contains("vo.java.vm")) {
            fileName = StringUtils.format("{}/domain/vo/{}Vo.java", javaPath, className);
        }
        if (template.contains("bo.java.vm")) {
            fileName = StringUtils.format("{}/domain/bo/{}Bo.java", javaPath, className);
        }
        if (template.contains("mapper.java.vm")) {
            fileName = StringUtils.format("{}/mapper/{}Mapper.java", javaPath, className);
        } else if (template.contains("service.java.vm")) {
            fileName = StringUtils.format("{}/service/I{}Service.java", javaPath, className);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
        } else if (template.contains("controller.java.vm")) {
            fileName = StringUtils.format("{}/controller/{}Controller.java", javaPath, className);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = StringUtils.format("{}/{}Mapper.xml", mybatisPath, className);
        } else if (template.contains("sql.vm")) {
            fileName = businessName + "Menu.sql";
        } else if (template.contains("api.ts.vm")) {
            fileName = StringUtils.format("{}/api/{}/{}/index.ts", vuePath, moduleName, businessName);
        } else if (template.contains("types.ts.vm")) {
            fileName = StringUtils.format("{}/api/{}/{}/types.ts", vuePath, moduleName, businessName);
        } else if (template.contains("index.vue.vm")) {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        } else if (template.contains("index-tree.vue.vm")) {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        return fileName;
    }

    private static String getScmFileName(String template, GenTable genTable) {
        Dict paramsObj = JsonUtils.parseMap(genTable.getOptions());
        String scmDomain = paramsObj.getStr(GenConstants.SCM_DOMAIN);
        if (StringUtils.isBlank(scmDomain)) {
            scmDomain = GenUtils.resolveScmDomain(genTable.getTableName());
        }
        String businessName = genTable.getBusinessName();
        String className = genTable.getClassName();
        String scmDomainCap = GenUtils.capitalizeDomain(scmDomain);
        String prefix = StringUtils.format("scm-gen/{}/{}/", scmDomain, businessName);
        String apiBase = prefix + "whl-scm/" + scmDomain + "/whl-scm-" + scmDomain + "-api/src/main/java/com/whl/scm/" + scmDomain + "/api";
        String bizBase = prefix + "whl-scm/" + scmDomain + "/whl-scm-" + scmDomain + "-biz/src/main/java/com/whl/scm/" + scmDomain + "/biz";
        String adapterBase = prefix + "whl-scm/" + scmDomain + "/whl-scm-" + scmDomain + "-admin-adapter/src/main/java/com/whl/scm/" + scmDomain + "/adapter/admin";
        String webBase = prefix + "admin-web/src";

        if (template.contains("ScmPageResult.java.vm")) {
            return apiBase + "/page/ScmPageResult.java";
        }
        if (template.contains("PageQuery.java.vm")) {
            return apiBase + "/query/Page" + className + "Query.java";
        }
        if (template.contains("GetQuery.java.vm")) {
            return apiBase + "/query/Get" + className + "Query.java";
        }
        if (template.contains("ListOptionsQuery.java.vm")) {
            return apiBase + "/query/List" + className + "OptionsQuery.java";
        }
        if (template.contains("CreateCommand.java.vm")) {
            return apiBase + "/command/Create" + className + "Command.java";
        }
        if (template.contains("UpdateCommand.java.vm")) {
            return apiBase + "/command/Update" + className + "Command.java";
        }
        if (template.contains("DeleteCommand.java.vm")) {
            return apiBase + "/command/Delete" + className + "Command.java";
        }
        if (template.contains("Dto.java.vm")) {
            return apiBase + "/dto/" + className + "Dto.java";
        }
        if (template.contains("Facade.java.vm")) {
            return apiBase + "/facade/" + className + "Facade.java";
        }
        if (template.contains("Entity.java.vm")) {
            return bizBase + "/domain/" + scmDomainCap + className + ".java";
        }
        if (template.contains("Mapper.java.vm")) {
            return bizBase + "/mapper/" + scmDomainCap + className + "Mapper.java";
        }
        if (template.contains("AppService.java.vm")) {
            return bizBase + "/" + className + "AppService.java";
        }
        if (template.contains("ScmController.java.vm")) {
            return adapterBase + "/controller/Scm" + className + "Controller.java";
        }
        if (template.contains("SaveRequest.java.vm")) {
            return adapterBase + "/controller/request/Save" + className + "Request.java";
        }
        if (template.contains("Vo.java.vm")) {
            return adapterBase + "/controller/vo/" + className + "Vo.java";
        }
        if (template.contains("scm_menu.sql.vm")) {
            return prefix + "script/sql/" + scmDomain + "/_gen/" + genTable.getTableName() + "_menu.sql";
        }
        if (template.contains("api.ts.vm")) {
            return webBase + "/api/scm/_gen/" + scmDomain + "-" + businessName + ".ts";
        }
        if (template.contains("types.snippet.vm")) {
            return prefix + "MERGE/types-" + scmDomain + "-" + businessName + ".snippet";
        }
        if (template.contains("api-merge.snippet.vm")) {
            return prefix + "MERGE/api-" + scmDomain + "-" + businessName + ".snippet";
        }
        if (template.contains("ErrorCode.snippet.vm")) {
            return prefix + "MERGE/ErrorCode-" + scmDomain + "-" + businessName + ".snippet";
        }
        if (template.contains("index.vue.vm")) {
            return webBase + "/views/scm/" + scmDomain + "/" + businessName + "/index.vue";
        }
        return prefix + template;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        return StringUtils.substring(packageName, 0, lastIndex);
    }

    /**
     * 根据列类型获取导入包
     *
     * @param genTable 业务表对象
     * @return 返回需要导入的包列表
     */
    public static HashSet<String> getImportList(GenTable genTable) {
        List<GenTableColumn> columns = genTable.getColumns();
        HashSet<String> importList = new HashSet<>();
        for (GenTableColumn column : columns) {
            if (!column.isSuperColumn() && GenConstants.TYPE_DATE.equals(column.getJavaType())) {
                importList.add("java.util.Date");
                importList.add("com.fasterxml.jackson.annotation.JsonFormat");
            } else if (!column.isSuperColumn() && GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType())) {
                importList.add("java.math.BigDecimal");
            } else if (!column.isSuperColumn() && "imageUpload".equals(column.getHtmlType())) {
                importList.add("com.whl.framework.translation.annotation.Translation");
                importList.add("com.whl.framework.translation.constant.TransConstant");
            }
        }
        return importList;
    }

    /**
     * 根据列类型获取字典组
     *
     * @param genTable 业务表对象
     * @return 返回字典组
     */
    public static String getDicts(GenTable genTable) {
        List<GenTableColumn> columns = genTable.getColumns();
        Set<String> dicts = new HashSet<>();
        addDicts(dicts, columns);
        return StringUtils.join(dicts, ", ");
    }

    /**
     * 添加字典列表
     *
     * @param dicts 字典列表
     * @param columns 列集合
     */
    public static void addDicts(Set<String> dicts, List<GenTableColumn> columns) {
        for (GenTableColumn column : columns) {
            if (!column.isSuperColumn() && StringUtils.isNotEmpty(column.getDictType()) && StringUtils.equalsAny(
                column.getHtmlType(),
                new String[] { GenConstants.HTML_SELECT, GenConstants.HTML_RADIO, GenConstants.HTML_CHECKBOX })) {
                dicts.add("'" + column.getDictType() + "'");
            }
        }
    }

    /**
     * 获取权限前缀
     *
     * @param moduleName   模块名称
     * @param businessName 业务名称
     * @return 返回权限前缀
     */
    public static String getPermissionPrefix(String moduleName, String businessName) {
        return StringUtils.format("{}:{}", moduleName, businessName);
    }

    /**
     * 获取上级菜单ID字段
     *
     * @param paramsObj 生成其他选项
     * @return 上级菜单ID字段
     */
    public static String getParentMenuId(Dict paramsObj) {
        if (CollUtil.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.PARENT_MENU_ID)
            && StringUtils.isNotEmpty(paramsObj.getStr(GenConstants.PARENT_MENU_ID))) {
            return paramsObj.getStr(GenConstants.PARENT_MENU_ID);
        }
        return DEFAULT_PARENT_MENU_ID;
    }

    /**
     * 获取树编码
     *
     * @param paramsObj 生成其他选项
     * @return 树编码
     */
    public static String getTreecode(Map<String, Object> paramsObj) {
        if (CollUtil.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.TREE_CODE)) {
            return StringUtils.toCamelCase(Convert.toStr(paramsObj.get(GenConstants.TREE_CODE)));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取树父编码
     *
     * @param paramsObj 生成其他选项
     * @return 树父编码
     */
    public static String getTreeParentCode(Dict paramsObj) {
        if (CollUtil.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.TREE_PARENT_CODE)) {
            return StringUtils.toCamelCase(paramsObj.getStr(GenConstants.TREE_PARENT_CODE));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取树名称
     *
     * @param paramsObj 生成其他选项
     * @return 树名称
     */
    public static String getTreeName(Dict paramsObj) {
        if (CollUtil.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.TREE_NAME)) {
            return StringUtils.toCamelCase(paramsObj.getStr(GenConstants.TREE_NAME));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取需要在哪一列上面显示展开按钮
     *
     * @param genTable 业务表对象
     * @return 展开按钮列序号
     */
    public static int getExpandColumn(GenTable genTable) {
        String options = genTable.getOptions();
        Dict paramsObj = JsonUtils.parseMap(options);
        String treeName = paramsObj.getStr(GenConstants.TREE_NAME);
        int num = 0;
        for (GenTableColumn column : genTable.getColumns()) {
            if (column.isList()) {
                num++;
                String columnName = column.getColumnName();
                if (columnName.equals(treeName)) {
                    break;
                }
            }
        }
        return num;
    }
}
