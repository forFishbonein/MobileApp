package com.tutoring.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    /**
     * 上传文件到阿里云OSS，返回文件的公开URL或可访问路径
     */
    String uploadFile(MultipartFile file);
}
