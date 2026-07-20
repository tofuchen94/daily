-- ========== MySQL 建表 (云托管环境) ==========

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    openid VARCHAR(128) NOT NULL UNIQUE,
    nickname VARCHAR(100) DEFAULT '',
    avatar_url VARCHAR(500) DEFAULT '',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 指标定义表（用户私有）
CREATE TABLE IF NOT EXISTS metric_definitions (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    unit VARCHAR(20) DEFAULT '',
    sort_order INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 每日记录表（用户私有，同用户同日期唯一）
CREATE TABLE IF NOT EXISTS daily_records (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    record_date VARCHAR(10) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 记录指标值表
CREATE TABLE IF NOT EXISTS record_metrics (
    id BIGINT PRIMARY KEY,
    record_id BIGINT NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value VARCHAR(500) NOT NULL DEFAULT '',
    unit VARCHAR(20) DEFAULT '',
    FOREIGN KEY (record_id) REFERENCES daily_records(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 模版表（用户私有）
CREATE TABLE IF NOT EXISTS templates (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    is_default INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
