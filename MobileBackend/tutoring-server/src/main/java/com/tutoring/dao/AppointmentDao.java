package com.tutoring.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutoring.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentDao extends BaseMapper<Appointment> {

}

