-- 指标定义表
CREATE TABLE IF NOT EXISTS metric_definitions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    unit TEXT DEFAULT '',
    sort_order INTEGER DEFAULT 0
);

-- 每日记录表
CREATE TABLE IF NOT EXISTS daily_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    record_date TEXT NOT NULL UNIQUE,
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- 记录指标值表
CREATE TABLE IF NOT EXISTS record_metrics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    record_id INTEGER NOT NULL,
    metric_name TEXT NOT NULL,
    metric_value TEXT NOT NULL DEFAULT '',
    unit TEXT DEFAULT '',
    FOREIGN KEY (record_id) REFERENCES daily_records(id) ON DELETE CASCADE
);

-- 模版表
CREATE TABLE IF NOT EXISTS templates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    content TEXT NOT NULL DEFAULT '',
    is_default INTEGER DEFAULT 0
);

-- 默认模版
INSERT OR IGNORE INTO templates (id, name, content, is_default)
VALUES (1, '默认模版', '【业绩日报】{date}
{销售额}
{客户数}
{备注}', 1);
