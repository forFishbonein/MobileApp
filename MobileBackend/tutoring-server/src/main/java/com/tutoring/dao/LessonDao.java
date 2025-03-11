package com.tutoring.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutoring.entity.Lesson;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface LessonDao extends BaseMapper<Lesson> {

}
