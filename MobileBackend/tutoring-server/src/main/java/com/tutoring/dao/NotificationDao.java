package com.tutoring.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutoring.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationDao extends BaseMapper<Notification> {
}

