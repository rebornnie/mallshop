package com.mall.admin.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface MinioService {

    Map<String, String> uploadFile(MultipartFile file);
}
