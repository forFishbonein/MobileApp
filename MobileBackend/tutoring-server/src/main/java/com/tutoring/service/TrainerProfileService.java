package com.tutoring.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.TrainerProfileQuery;
import com.tutoring.entity.TrainerProfile;
import com.tutoring.entity.User;
import com.tutoring.vo.TrainerAllProfile;
import com.tutoring.vo.TrainerProfileVO;

import java.util.List;

public interface TrainerProfileService extends IService<TrainerProfile> {
    public void createDefaultTrainerProfile(Long userId, String name);

    public TrainerAllProfile getTrainerAllProfile(Long userId);

    public Page<TrainerProfileVO> listTrainers(TrainerProfileQuery query);
}
