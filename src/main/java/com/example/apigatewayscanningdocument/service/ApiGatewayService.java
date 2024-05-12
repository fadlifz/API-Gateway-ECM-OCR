package com.example.apigatewayscanningdocument.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.apigatewayscanningdocument.request.AllRequest;
import com.example.apigatewayscanningdocument.response.EcmResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

import org.springframework.http.HttpEntity;

@Service
public class ApiGatewayService implements Serializable {

    public String callApiOcr(AllRequest fileRequest) {
        try {
            String apiUrl = "http://localhost:8081/extract-text";
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<AllRequest> fileRequestHttpEntity = new HttpEntity<>(fileRequest);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, fileRequestHttpEntity,
                    String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("extractedText").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public EcmResponse callApiEcm(AllRequest fileRequest) {
        try {
            String apiUrl = "http://localhost:8082/savePdfText";
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<AllRequest> fileRequestHttpEntity = new HttpEntity<>(fileRequest);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, fileRequestHttpEntity,
                    String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return EcmResponse.builder()
                    .txtFile(jsonNode.get("result").get("txtFile").asText())
                    .pdfFile(jsonNode.get("result").get("pdfFile").asText())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
