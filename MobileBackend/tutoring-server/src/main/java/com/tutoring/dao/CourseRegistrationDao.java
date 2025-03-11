package com.tutoring.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutoring.entity.CourseRegistration;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseRegistrationDao extends BaseMapper<CourseRegistration> {

}