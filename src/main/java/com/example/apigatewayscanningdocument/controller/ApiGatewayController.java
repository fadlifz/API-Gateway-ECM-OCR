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
import org.springframework.web.bind.annotation.RequestHeader;
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
    ResponseEntity<Map<String, Object>> scanFile(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("file") MultipartFile file,
            @RequestParam("requestId") String requestId)
            throws IOException {
                //sebelum eksekusi di bawah ambil token dulu
//bikin 1 function lagi yang outputnya token dan diambil dari parameter dari postman(ambil dari partner token adira)
// setelah didapat token lalu execute callApiOcr
// tapi nanti urlnya ganti pake url scan ktp adira tapi yang token pake yang akses token

        Map<String, Object> jsonMap = new HashMap<>();
        if (!authorizationHeader.isEmpty()) {
            String text = extractionService.callApiOcr(authorizationHeader, file, requestId);
            jsonMap.put("extractedText", text);
            return new ResponseEntity<>(jsonMap, HttpStatus.OK);
        }
        jsonMap.put("extractedText", null);
        return new ResponseEntity<>(jsonMap, HttpStatus.BAD_REQUEST);
    }

    // @PostMapping("/savePdfText")
    // ResponseEntity<Map<String, Object>> savePdfText(@RequestBody AllRequest file)
    // throws IOException {
    // EcmResponse text = extractionService.callApiEcm(file);
    // Map<String, Object> jsonMap = new HashMap<>();
    // jsonMap.put("extractedText", text);
    // return new ResponseEntity<>(jsonMap, HttpStatus.OK);
    // }
}
