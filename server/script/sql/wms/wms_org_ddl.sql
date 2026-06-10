-- B1 组织主数据 wms_org（domains/wms/design/B1-org/TECH.md §14.3）
-- 列默认：BIGINT/INT/SMALLINT 0 · VARCHAR '' · 见 DATABASE.md §3.5
-- 导入：mysql --default-character-set=utf8mb4 ... < wms_org_ddl.sql

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS wms_org (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    tenant_id       VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '租户编号',
    org_code        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '组织编码，租户内唯一',
    org_name        VARCHAR(128) NOT NULL DEFAULT '' COMMENT '组织名称',
    org_type        SMALLINT     NOT NULL DEFAULT 0 COMMENT '10集团 20法人 30事业部 40区域',
    parent_org_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '上级组织id，0=顶级',
    linked_dept_id  BIGINT       NOT NULL DEFAULT 0 COMMENT '关联 sys_dept.dept_id，0=未关联',
    status          SMALLINT     NOT NULL DEFAULT 10 COMMENT '10启用 20停用',
    sort_order      INT          NOT NULL DEFAULT 0 COMMENT '排序',
    remark          VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
    create_dept     BIGINT       DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    del_flag        CHAR(1)      NOT NULL DEFAULT '0' COMMENT '0正常 2删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_wms_org_code (tenant_id, org_code),
    KEY idx_wms_org_parent (tenant_id, parent_org_id),
    KEY idx_wms_org_status (tenant_id, status)
) ENGINE=InnoDB COMMENT='WMS组织主数据';
