package com.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daily.entity.Template;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TemplateMapper extends BaseMapper<Template> {

    @Select("SELECT * FROM templates WHERE is_default = 1 LIMIT 1")
    Template findDefault();

    @Update("UPDATE templates SET is_default = 0 WHERE is_default = 1")
    void clearDefault();
}
