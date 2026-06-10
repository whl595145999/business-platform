-- prd_sku_barcode（domains/product/prd/B2-product/PRODUCT.md §2.13.4）
-- 列默认：BIGINT/INT/SMALLINT/TINYINT 0 · VARCHAR '' · 见 DATABASE.md §3.5
-- 导入：mysql --default-character-set=utf8mb4 ... < prd_sku_barcode_ddl.sql

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS prd_sku_barcode (
    id              BIGINT         NOT NULL COMMENT '主键',
    tenant_id       VARCHAR(20)    NOT NULL DEFAULT '' COMMENT '租户编号',
    sku_id          BIGINT         NOT NULL DEFAULT 0 COMMENT '货品 prd_sku.id',
    barcode         VARCHAR(32)    NOT NULL DEFAULT '' COMMENT '条码值，租户内全局唯一',
    default_flag    TINYINT        NOT NULL DEFAULT 0 COMMENT '是否默认扫码：1-是,0-否',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '启停：1-启用,0-停用',
    remark          VARCHAR(500)   NOT NULL DEFAULT '' COMMENT '备注',
    create_dept     BIGINT         DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT         DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME       DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT         DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME       DEFAULT NULL COMMENT '更新时间',
    del_flag        CHAR(1)        NOT NULL DEFAULT '0' COMMENT '0-正常,2-删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_sku_barcode_tenant_barcode (tenant_id, barcode),
    KEY idx_prd_sku_barcode_tenant_sku (tenant_id, sku_id),
    KEY idx_prd_sku_barcode_tenant_sku_default (tenant_id, sku_id, default_flag)
) ENGINE=InnoDB COMMENT='货品条码注册';
