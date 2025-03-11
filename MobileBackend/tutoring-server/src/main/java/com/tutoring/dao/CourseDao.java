package com.tutoring.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutoring.entity.Course;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CourseDao extends BaseMapper<Course> {

}
