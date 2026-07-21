package com.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daily.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE openid = #{openid} LIMIT 1")
    User findByOpenid(@Param("openid") String openid);
}
