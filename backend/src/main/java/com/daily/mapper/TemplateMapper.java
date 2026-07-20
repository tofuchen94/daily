package com.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daily.entity.Template;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TemplateMapper extends BaseMapper<Template> {

    @Select("SELECT * FROM templates WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    Template findDefault(@Param("userId") Long userId);

    @Update("UPDATE templates SET is_default = 0 WHERE user_id = #{userId} AND is_default = 1")
    void clearDefault(@Param("userId") Long userId);
}
