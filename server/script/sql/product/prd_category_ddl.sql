-- prd_category（domains/product/prd/B2-product/PRODUCT.md §2.11.6）
-- 列默认：BIGINT/INT/SMALLINT/TINYINT 0 · VARCHAR '' · 见 DATABASE.md §3.5
-- 导入：mysql --default-character-set=utf8mb4 ... < prd_category_ddl.sql

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS prd_category (
    id                          BIGINT         NOT NULL COMMENT '主键',
    tenant_id                   VARCHAR(20)    NOT NULL DEFAULT '' COMMENT '租户编号',
    category_code               VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '类目编码，租户内唯一',
    category_name               VARCHAR(128)   NOT NULL DEFAULT '' COMMENT '类目名称',
    parent_id                   BIGINT         NOT NULL DEFAULT 0 COMMENT '上级类目id，0=顶级',
    status                      SMALLINT       NOT NULL DEFAULT 10 COMMENT '启停：10-启用,20-停用',
    sort_order                  INT            NOT NULL DEFAULT 0 COMMENT '排序',
    remark                      VARCHAR(500)   NOT NULL DEFAULT '' COMMENT '备注',
    default_item_class          SMALLINT       NOT NULL DEFAULT 0 COMMENT '默认货品类型：10-贸易,20-物料,30-包材,40-药品,50-虚拟，0=不继承',
    default_product_type        SMALLINT       NOT NULL DEFAULT 0 COMMENT '默认履约类型：10-实物,20-虚拟，0=不继承',
    default_expiry_flag         TINYINT        NOT NULL DEFAULT 0 COMMENT '默认是否效期品：1-是,0-否',
    default_barcode_policy      SMALLINT       NOT NULL DEFAULT 0 COMMENT '默认条码策略：10-必须有国标码,20-可无，0=不继承',
    default_shelf_life_value    INT            NOT NULL DEFAULT 0 COMMENT '默认保质期数值',
    default_shelf_life_unit     SMALLINT       NOT NULL DEFAULT 0 COMMENT '默认保质期单位：10-小时,20-天',
    default_near_expiry_value   INT            NOT NULL DEFAULT 0 COMMENT '默认临期预警数值',
    default_near_expiry_unit    SMALLINT       NOT NULL DEFAULT 0 COMMENT '默认临期预警单位：10-小时,20-天',
    create_dept                 BIGINT         DEFAULT NULL COMMENT '创建部门',
    create_by                   BIGINT         DEFAULT NULL COMMENT '创建者',
    create_time                 DATETIME       DEFAULT NULL COMMENT '创建时间',
    update_by                   BIGINT         DEFAULT NULL COMMENT '更新者',
    update_time                 DATETIME       DEFAULT NULL COMMENT '更新时间',
    del_flag                    CHAR(1)        NOT NULL DEFAULT '0' COMMENT '0-正常,2-删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_category_tenant_code (tenant_id, category_code),
    KEY idx_prd_category_tenant_parent (tenant_id, parent_id),
    KEY idx_prd_category_tenant_status (tenant_id, status)
) ENGINE=InnoDB COMMENT='货品类目树';
