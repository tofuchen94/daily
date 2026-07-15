-- ========== MySQL 建表 (云托管环境) ==========

CREATE TABLE IF NOT EXISTS metric_definitions (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit VARCHAR(20) DEFAULT '',
    sort_order INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS daily_records (
    id BIGINT PRIMARY KEY,
    record_date VARCHAR(10) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS record_metrics (
    id BIGINT PRIMARY KEY,
    record_id BIGINT NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value VARCHAR(500) NOT NULL DEFAULT '',
    unit VARCHAR(20) DEFAULT '',
    FOREIGN KEY (record_id) REFERENCES daily_records(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS templates (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    is_default INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 默认模版 (用 INSERT IGNORE 避免重复)
INSERT IGNORE INTO templates (id, name, content, is_default)
VALUES (1, '默认模版', CONCAT('【业绩日报】{date}\n{销售额}\n{客户数}\n{备注}'), 1);
