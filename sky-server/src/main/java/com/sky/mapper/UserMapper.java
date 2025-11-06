package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    void insert(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    @Select("select count(id) from user where create_time > #{begin} and create_time < #{end}")
    Integer getNewUserByDate(HashMap<Object, Object> map);

    @Select("select count(id) from user where create_time < #{end}")
    Integer getTotalUserByDate(HashMap<Object, Object> map);
}
