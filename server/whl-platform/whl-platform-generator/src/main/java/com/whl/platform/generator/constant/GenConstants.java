package com.whl.platform.generator.constant;

/**
 * 代码生成通用常量
 *
 * @author ruoyi
 */
public interface GenConstants {
    /**
     * 单表（增删改查）
     */
    String TPL_CRUD = "crud";

    /**
     * 树表（增删改查）
     */
    String TPL_TREE = "tree";

    /**
     * SCM 单表 Facade CRUD（分页 + REST）
     */
    String TPL_SCM_CRUD = "scm_crud";

    /**
     * SCM 域（wms / oms / inventory）
     */
    String SCM_DOMAIN = "scmDomain";

    /**
     * REST 资源名（复数，如 orgs）
     */
    String SCM_RESOURCE = "scmResource";

    /**
     * 菜单 ID（1700+ 段）
     */
    String SCM_MENU_ID = "scmMenuId";

    /**
     * 菜单排序
     */
    String SCM_MENU_ORDER = "scmMenuOrder";

    /**
     * 前端 component 路径
     */
    String SCM_MENU_COMPONENT = "scmMenuComponent";

    /**
     * 路径参数业务键列名
     */
    String SCM_BUSINESS_KEY = "scmBusinessKey";

    /**
     * 权限前缀
     */
    String SCM_PERM_PREFIX = "scmPermPrefix";

    /**
     * 是否生成 options 下拉接口
     */
    String SCM_GENERATE_OPTIONS = "scmGenerateOptions";

    /**
     * 树编码字段
     */
    String TREE_CODE = "treeCode";

    /**
     * 树父编码字段
     */
    String TREE_PARENT_CODE = "treeParentCode";

    /**
     * 树名称字段
     */
    String TREE_NAME = "treeName";

    /**
     * 上级菜单ID字段
     */
    String PARENT_MENU_ID = "parentMenuId";

    /**
     * 上级菜单名称字段
     */
    String PARENT_MENU_NAME = "parentMenuName";

    /**
     * 数据库字符串类型
     */
    String[] COLUMNTYPE_STR = {"char", "varchar", "enum", "set", "nchar", "nvarchar", "varchar2", "nvarchar2"};

    /**
     * 数据库文本类型
     */
    String[] COLUMNTYPE_TEXT = {"tinytext", "text", "mediumtext", "longtext", "binary", "varbinary", "blob",
        "ntext", "image", "bytea"};

    /**
     * 数据库时间类型
     */
    String[] COLUMNTYPE_TIME = {"datetime", "time", "date", "timestamp", "year", "interval",
        "smalldatetime", "datetime2", "datetimeoffset", "timestamptz"};

    /**
     * 数据库数字类型
     */
    String[] COLUMNTYPE_NUMBER = {"tinyint", "smallint", "mediumint", "int", "int2", "int4", "int8", "number", "integer",
        "bit", "bigint", "float", "float4", "float8", "double", "decimal", "numeric", "real", "double precision",
        "smallserial", "serial", "bigserial", "money", "smallmoney"};

    /**
     * BO对象 不需要添加字段
     */
    String[] COLUMNNAME_NOT_ADD = {"create_dept", "create_by", "create_time", "del_flag", "update_by",
        "update_time", "version", "tenant_id"};

    /**
     * BO对象 不需要编辑字段
     */
    String[] COLUMNNAME_NOT_EDIT = {"create_dept", "create_by", "create_time", "del_flag", "update_by",
        "update_time", "version", "tenant_id"};

    /**
     * VO对象 不需要返回字段
     */
    String[] COLUMNNAME_NOT_LIST = {"create_dept", "create_by", "create_time", "del_flag", "update_by",
        "update_time", "version", "tenant_id"};

    /**
     * BO对象 不需要查询字段
     */
    String[] COLUMNNAME_NOT_QUERY = {"id", "create_dept", "create_by", "create_time", "del_flag", "update_by",
        "update_time", "remark", "version", "tenant_id"};

    /**
     * Entity基类字段
     */
    String[] BASE_ENTITY = {"createDept", "createBy", "createTime", "updateBy", "updateTime", "tenantId"};

    /**
     * 文本框
     */
    String HTML_INPUT = "input";

    /**
     * 文本域
     */
    String HTML_TEXTAREA = "textarea";

    /**
     * 下拉框
     */
    String HTML_SELECT = "select";

    /**
     * 单选框
     */
    String HTML_RADIO = "radio";

    /**
     * 复选框
     */
    String HTML_CHECKBOX = "checkbox";

    /**
     * 日期控件
     */
    String HTML_DATETIME = "datetime";

    /**
     * 图片上传控件
     */
    String HTML_IMAGE_UPLOAD = "imageUpload";

    /**
     * 文件上传控件
     */
    String HTML_FILE_UPLOAD = "fileUpload";

    /**
     * 富文本控件
     */
    String HTML_EDITOR = "editor";

    /**
     * 部门树选择
     */
    String HTML_DEPT_TREE = "deptTree";

    /**
     * 组织下拉
     */
    String HTML_ORG_SELECT = "orgSelect";

    /**
     * 租户下拉
     */
    String HTML_TENANT_SELECT = "tenantSelect";

    /**
     * 字符串类型
     */
    String TYPE_STRING = "String";

    /**
     * 整型
     */
    String TYPE_INTEGER = "Integer";

    /**
     * 长整型
     */
    String TYPE_LONG = "Long";

    /**
     * 浮点型
     */
    String TYPE_DOUBLE = "Double";

    /**
     * 高精度计算类型
     */
    String TYPE_BIGDECIMAL = "BigDecimal";

    /**
     * 时间类型
     */
    String TYPE_DATE = "Date";

    /**
     * 模糊查询
     */
    String QUERY_LIKE = "LIKE";

    /**
     * 相等查询
     */
    String QUERY_EQ = "EQ";

    /**
     * 需要
     */
    String REQUIRE = "1";
}
