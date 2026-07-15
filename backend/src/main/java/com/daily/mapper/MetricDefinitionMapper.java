package com.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daily.entity.MetricDefinition;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MetricDefinitionMapper extends BaseMapper<MetricDefinition> {

    @Select("SELECT * FROM metric_definitions ORDER BY sort_order")
    List<MetricDefinition> findAllOrdered();

    @Delete("DELETE FROM metric_definitions")
    void deleteAll();
}
