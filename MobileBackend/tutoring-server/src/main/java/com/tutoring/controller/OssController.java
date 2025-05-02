package com.tutoring.controller;

import com.tutoring.result.RestResult;
import com.tutoring.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/oss")
@Slf4j
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("/uploadImage")
    public RestResult<String> uploadImage(@RequestPart("file") MultipartFile file) {
        String fileUrl = ossService.uploadFile(file);
        return RestResult.success(fileUrl, "Image uploaded successfully.");
    }

    @PostMapping("/uploadPdf")
    public RestResult<String> uploadPdf(@RequestPart("file") MultipartFile file) {
        String fileUrl = ossService.uploadFile(file);
        return RestResult.success(fileUrl, "PDF uploaded successfully.");
    }
}
