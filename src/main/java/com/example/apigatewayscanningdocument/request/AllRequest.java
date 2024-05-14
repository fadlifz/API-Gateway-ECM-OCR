package com.example.apigatewayscanningdocument.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AllRequest {

    private MultipartFile file;
    private String requestId;

}
