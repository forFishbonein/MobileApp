package com.tutoring.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
public class OssServiceImpl implements OssService {

    @Value("${ali-oss.endpoint}")
    private String endpoint;

@Value("${ali-oss.access-key-id}")
    private String accessKeyId;

    @Value("${ali-oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${ali-oss.bucket-name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unknown.file";
        }

        String objectName = LocalDate.now() + "/" + UUID.randomUUID() + "_" + originalFilename;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucketName, objectName, inputStream);

            String fileUrl = "https://" + bucketName + "." + endpoint + "/" + objectName;
            return fileUrl;
        } catch (IOException e) {
            log.error("上传到OSS失败: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "File upload failed: " + e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }
}
