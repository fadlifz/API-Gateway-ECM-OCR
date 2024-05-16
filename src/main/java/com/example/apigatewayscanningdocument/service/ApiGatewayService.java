package com.example.apigatewayscanningdocument.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.List;

@Service
public class ApiGatewayService implements Serializable {

    @Value("${api.ocr.urlToken}")
    private String apiUrlToken;

    @Value("${api.ocr.urlKtp}")
    private String apiUrlKtp;

    @Value("${api.ocr.urlBpkb}")
    private String apiUrlBpkb;

    @Value("${api.ocr.urlFakturKendaraan}")
    private String apiUrlFakturKendaraan;

    @Value("${api.ecm.apiKey}")
    private String apiKey;

    @Value("${api.ecm.uploadFile}")
    private String apiEcmUploadFile;

    public String getToken() {
    try {

        RestTemplate restTemplate = new RestTemplate();

        Encoder encoder = Base64.getEncoder();
        String username = "4ELct1DD7b4g0mGV";
        String password = "TaGBoUCL3jePg51t";
        String originalString = username+":"+password;
        String encodedString = encoder.encodeToString(originalString.getBytes());

        String headerAthorization="Basic "+encodedString;

        // BasicAuth
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "4kWHq8vIjV94NiosmOORZi5laxaP8l0dIZIjysTv");
        headers.set("Authorization", headerAthorization);
        
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("scope", "partner_api offline_access");

        HttpEntity<MultiValueMap<String, Object>> fileRequestHttpEntity = new HttpEntity<>(map,headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrlToken, HttpMethod.POST, fileRequestHttpEntity,
                String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

    public Map<String, Object> callApi(List<MultipartFile> file, String requestId, String documentType, String documentTitle, String name, String application, String objectStore, String region, String apiType) {
        Map<String, Object> resultMap = new HashMap<>();
        String apiUrl = "";
        FileManagementService fileManagementService = new FileManagementService();
        File manageFile = null;
        try {
            String testToken;
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    
            //just in case handling multiple files should be done in BE
            // if (file.size() == 1) {
            //     manageFile = fileManagementService.manageSingleFile(file);    
            // }else{
            //     if(fileManagementService.getFileExtension(null))
            // }

            manageFile = fileManagementService.manageSingleFile(file);
            
                // manageFile = fileManagementService.mergeFiles(file);
    
            if ("ocr".equals(apiType)) {
                testToken = getToken();
                if (testToken == null) {
                    resultMap.put("status", "fail");
                    return resultMap;
                }
                headers.set("Authorization", "Bearer " + testToken);
                switch (documentType) {
                    case "0":
                        apiUrl = apiUrlKtp;
                        break;
                    case "1":
                        apiUrl = apiUrlBpkb;
                        break;
                    case "2":
                        apiUrl = apiUrlFakturKendaraan;
                        break;
                    default:
                        break;
                }
            } else if ("ecm".equals(apiType)) {
                testToken = apiKey;
                if (testToken == null) {
                    resultMap.put("status", "fail");
                    return resultMap;
                }
                headers.set("x-api-key", testToken);
                apiUrl = apiEcmUploadFile;
                map.add("documentTitle", documentTitle);
                map.add("name", name);
                map.add("application", application);
                map.add("objectStore", objectStore);
                map.add("region", region);
            } else {
                resultMap.put("status", "error");
                resultMap.put("message", "Invalid apiType");
                return resultMap;
            }
    
            RestTemplate restTemplate = new RestTemplate();
            map.add("file", new FileSystemResource(manageFile));
            map.add("requestId", requestId);
            map.add("documentType", documentType);
    
            HttpEntity<MultiValueMap<String, Object>> fileRequestHttpEntity = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, fileRequestHttpEntity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            resultMap = objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {});
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "error");
            resultMap.put("message", e.getMessage());
            return resultMap;
        }
    }

}
