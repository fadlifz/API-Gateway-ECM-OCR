package com.example.apigatewayscanningdocument.request;

import lombok.Data;

@Data
public class TokenRequest {

    private String grant_type;
    private String scope;

}
