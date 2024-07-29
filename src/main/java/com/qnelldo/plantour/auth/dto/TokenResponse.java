package com.qnelldo.plantour.auth.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private String id_token;
}