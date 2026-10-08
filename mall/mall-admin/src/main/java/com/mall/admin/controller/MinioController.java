package com.mall.admin.controller;

import com.mall.admin.service.MinioService;
import com.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "MinIO文件上传接口")
public class MinioController {

    private final MinioService minioService;

    @PostMapping("/image")
    @Operation(summary = "上传图片")
    public CommonResult<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> result = minioService.uploadFile(file);
        if (result == null) {
            return CommonResult.failed("上传文件为空");
        }
        return CommonResult.success(result);
    }
}
