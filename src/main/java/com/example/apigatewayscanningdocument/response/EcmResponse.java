package com.example.apigatewayscanningdocument.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EcmResponse {
    private String txtFile;
    private String pdfFile;
}
