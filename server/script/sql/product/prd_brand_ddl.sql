-- prd_brand（domains/product/prd/B2-product/PRODUCT.md §2.12.4）
-- 列默认：BIGINT/INT/SMALLINT/TINYINT 0 · VARCHAR '' · 见 DATABASE.md §3.5
-- 导入：mysql --default-character-set=utf8mb4 ... < prd_brand_ddl.sql

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS prd_brand (
    id              BIGINT         NOT NULL COMMENT '主键',
    tenant_id       VARCHAR(20)    NOT NULL DEFAULT '' COMMENT '租户编号',
    brand_code      VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '品牌编码，租户内唯一',
    brand_name      VARCHAR(128)   NOT NULL DEFAULT '' COMMENT '品牌名称',
    status          SMALLINT       NOT NULL DEFAULT 10 COMMENT '启停：10-启用,20-停用',
    sort_order      INT            NOT NULL DEFAULT 0 COMMENT '排序',
    remark          VARCHAR(500)   NOT NULL DEFAULT '' COMMENT '备注',
    create_dept     BIGINT         DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT         DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME       DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT         DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME       DEFAULT NULL COMMENT '更新时间',
    del_flag        CHAR(1)        NOT NULL DEFAULT '0' COMMENT '0-正常,2-删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_brand_tenant_code (tenant_id, brand_code),
    KEY idx_prd_brand_tenant_status (tenant_id, status)
) ENGINE=InnoDB COMMENT='货品品牌';
