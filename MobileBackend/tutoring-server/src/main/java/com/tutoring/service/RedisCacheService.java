package com.tutoring.service;


import com.tutoring.entity.User;

public interface RedisCacheService {
    User getUser(Long userId);

}
