package com.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daily.entity.DailyRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface DailyRecordMapper extends BaseMapper<DailyRecord> {

    @Select("SELECT * FROM daily_records WHERE user_id = #{userId} AND record_date = #{date}")
    DailyRecord findByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Select("SELECT * FROM daily_records WHERE user_id = #{userId} AND record_date LIKE CONCAT(#{month}, '%') ORDER BY record_date DESC")
    List<DailyRecord> findByMonth(@Param("userId") Long userId, @Param("month") String month);
}
