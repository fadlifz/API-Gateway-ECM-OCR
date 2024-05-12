package com.example.apigatewayscanningdocument.controller;

import com.example.apigatewayscanningdocument.request.AllRequest;
import com.example.apigatewayscanningdocument.response.EcmResponse;
import com.example.apigatewayscanningdocument.service.ApiGatewayService;

import net.sourceforge.tess4j.TesseractException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiGatewayController {

    @Autowired
    private ApiGatewayService extractionService;

    @PostMapping("/scanFile")
    ResponseEntity<Map<String, Object>> scanFile(@RequestBody AllRequest file)
            throws IOException {
        String text = extractionService.callApiOcr(file);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("extractedText", text);
        return new ResponseEntity<>(jsonMap, HttpStatus.OK);
    }

    @PostMapping("/savePdfText")
    ResponseEntity<Map<String, Object>> savePdfText(@RequestBody AllRequest file)
            throws IOException {
        EcmResponse text = extractionService.callApiEcm(file);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("extractedText", text);
        return new ResponseEntity<>(jsonMap, HttpStatus.OK);
    }
}
