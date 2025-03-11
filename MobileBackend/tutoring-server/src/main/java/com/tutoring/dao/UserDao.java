package com.tutoring.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutoring.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
