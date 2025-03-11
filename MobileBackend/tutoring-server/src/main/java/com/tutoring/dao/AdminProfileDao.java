package com.tutoring.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutoring.entity.AdminProfile;
import com.tutoring.entity.FitnessCentre;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminProfileDao extends BaseMapper<AdminProfile> {
}
