package com.daily.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("metric_definitions")
public class MetricDefinition {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String unit;

    private Integer sortOrder;

    public MetricDefinition() {}

    public MetricDefinition(String name, String unit, Integer sortOrder) {
        this.name = name;
        this.unit = unit;
        this.sortOrder = sortOrder;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
