package com.tutoring.json;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info(metaObject.toString());
        if (metaObject.hasSetter("createdAt")) {
            metaObject.setValue("createdAt", LocalDateTime.now());
        }
        if (metaObject.hasSetter("updatedAt")) {
            metaObject.setValue("updatedAt", LocalDateTime.now());
        }
        if (metaObject.hasSetter("recordedAt")) {
            metaObject.setValue("recordedAt", LocalDateTime.now());
        }
        if (metaObject.hasSetter("sentAt")) {
            metaObject.setValue("sentAt", LocalDateTime.now());
        }
        if (metaObject.hasSetter("createUser")) {
            metaObject.setValue("createUser", SecurityContextHolder
                    .getContext()
                    .getAuthentication().getPrincipal());
        }
        if (metaObject.hasSetter("updateUser")) {
            metaObject.setValue("updateUser", SecurityContextHolder
                    .getContext()
                    .getAuthentication().getPrincipal());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info(metaObject.toString());

        if (metaObject.hasSetter("updatedAt")) {
            metaObject.setValue("updatedAt", LocalDateTime.now());
        }
        if (metaObject.hasSetter("updateUser")) {
            metaObject.setValue("updateUser", SecurityContextHolder
                    .getContext()
                    .getAuthentication().getPrincipal());
        }
    }
}
