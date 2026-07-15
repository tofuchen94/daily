package com.daily.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("record_metrics")
public class RecordMetric {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long recordId;

    private String metricName;

    private String metricValue;

    private String unit;

    public RecordMetric() {}

    public RecordMetric(Long recordId, String metricName, String metricValue, String unit) {
        this.recordId = recordId;
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.unit = unit;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }

    public String getMetricValue() { return metricValue; }
    public void setMetricValue(String metricValue) { this.metricValue = metricValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
