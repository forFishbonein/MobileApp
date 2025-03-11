package com.tutoring.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.FitnessCentreDao;
import com.tutoring.entity.FitnessCentre;
import com.tutoring.service.FitnessCentreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FitnessCentreServiceImpl extends ServiceImpl<FitnessCentreDao, FitnessCentre>
        implements FitnessCentreService {
}
