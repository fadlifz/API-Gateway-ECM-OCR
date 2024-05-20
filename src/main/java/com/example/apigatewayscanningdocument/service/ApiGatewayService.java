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

    @Value("${api.ocr.urlFakturHeInvoice}")
    private String apiUrlFakturHeInvoice;

    @Value("${api.ecm.apiKey}")
    private String apiKey;

    @Value("${api.ecm.uploadFile}")
    private String apiEcmUploadFile;

    @Value("${api.getToken.username}")
    private String apigetTokenUsername;
    
    @Value("${api.getToken.password}")
    private String apigetTokenPassword;
   
    @Value("${api.getToken.xApiKey}")
    private String apigetTokenXApiKey;
    
    @Value("${api.getToken.grantType}")
    private String apigetTokenGrantType;
    
    @Value("${api.getToken.scope}")
    private String apigetTokenScope;

    public String getToken() {
    try {

        RestTemplate restTemplate = new RestTemplate();

        Encoder encoder = Base64.getEncoder();
        String originalString = apigetTokenUsername+":"+apigetTokenPassword;
        String encodedString = encoder.encodeToString(originalString.getBytes());

        String headerAthorization="Basic "+encodedString;

        // BasicAuth
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apigetTokenXApiKey);
        headers.set("Authorization", headerAthorization);
        
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", apigetTokenGrantType);
        map.add("scope", apigetTokenScope);

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

            manageFile = fileManagementService.manageSingleFile(file);
    
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
                    case "3":
                        apiUrl = apiUrlFakturHeInvoice;
                        break;
                    default:
                        break;
                }
                map.add("documentType", documentType);
                map.add("requestId", requestId);
            } else if ("ecm".equals(apiType)) {
                testToken = apiKey;
                if (testToken == null) {
                    resultMap.put("status", "fail");
                    return resultMap;
                }
                headers.set("x-api-key", testToken);
                apiUrl = apiEcmUploadFile;
                map.add("documentTitle", documentTitle);
                map.add("nama", "Arifin");
                map.add("application", "POC_CMS_BPKB");
                map.add("objectStore", "ADIRAOS");
                map.add("region", "0100 - Jabodetabek");
                map.add("documentType", "BPKBUtamaCustomer");
                map.add("requestId", "123");
            } else {
                resultMap.put("status", "error");
                resultMap.put("message", "Invalid apiType");
                return resultMap;
            }
    
            RestTemplate restTemplate = new RestTemplate();
            map.add("file", new FileSystemResource(manageFile));
    
            HttpEntity<MultiValueMap<String, Object>> fileRequestHttpEntity = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, fileRequestHttpEntity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            resultMap = objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {});
            manageFile.delete();
            return resultMap;            
        } catch (Exception e) {
            e.printStackTrace();
            manageFile.delete();
            resultMap.put("status", "error");
            resultMap.put("message", e.getMessage());
            
            return resultMap;
        }
        
    }

}
