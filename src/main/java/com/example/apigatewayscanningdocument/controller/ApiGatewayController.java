package com.example.apigatewayscanningdocument.controller;

import com.example.apigatewayscanningdocument.service.ApiGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiGatewayController {

    @Autowired
    private ApiGatewayService extractionService;

    @CrossOrigin(origins={
        "*",
        "http://127.0.0.1:5173",
        "http://localhost:5173",
        "http://10.15.130.157:443",
        "http://10.15.130.157:80",
        "https://homelabs.weekendlabs.cloud"})
    @PostMapping("/scanDocument")
    ResponseEntity<Map<String, Object>> ocrKtp(@RequestParam("RemoteFile") List<MultipartFile> file,
            @RequestParam("requestId") String requestId, @RequestParam("documentType") String documentType)
            throws IOException {
                
                // Check if documentType is not provided or not in the allowed values
                if (documentType == null || documentType.isEmpty() || (!documentType.equals("0") && !documentType.equals("1") && !documentType.equals("2") && !documentType.equals("3"))) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("info", "Invalid or undefined document type!");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                Map<String, Object> result = extractionService.callApi(file, requestId,documentType, "","","","","","ocr");
                return new ResponseEntity<>(result, HttpStatus.OK);
           
    }
    
    @CrossOrigin(origins={
        "*",
        "http://127.0.0.1:5173",
        "http://localhost:5173",
        "http://10.15.130.157:443",
        "http://10.15.130.157:80",
        "https://homelabs.weekendlabs.cloud"})
    @PostMapping("/saveDocument")
    ResponseEntity<Map<String, Object>> callEcm(@RequestParam("RemoteFile") List<MultipartFile> file,
            @RequestParam("requestId") String requestId, @RequestParam("documentType") String documentType,
            @RequestParam("documentTitle") String documentTitle, @RequestParam("application") String application,
            @RequestParam("objectStore") String objectStore, @RequestParam("nama") String nama, @RequestParam("region") String region)
            throws IOException {
                Map<String, Object> result = extractionService.callApi(file, requestId,documentType, documentTitle,nama,application,objectStore,region,"ecm");
                return new ResponseEntity<>(result, HttpStatus.OK);
           
           
    }
}
