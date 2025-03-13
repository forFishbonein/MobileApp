package com.tutoring.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.TeacherEmailDao;
import com.tutoring.entity.TeacherEmail;
import com.tutoring.service.TeacherEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class TeacherEmailServiceImpl extends ServiceImpl<TeacherEmailDao, TeacherEmail>
        implements TeacherEmailService {
}
