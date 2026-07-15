package com.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daily.entity.RecordMetric;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RecordMetricMapper extends BaseMapper<RecordMetric> {

    @Select("SELECT * FROM record_metrics WHERE record_id = #{recordId}")
    List<RecordMetric> findByRecordId(@Param("recordId") Long recordId);

    @Delete("DELETE FROM record_metrics WHERE record_id = #{recordId}")
    void deleteByRecordId(@Param("recordId") Long recordId);
}
