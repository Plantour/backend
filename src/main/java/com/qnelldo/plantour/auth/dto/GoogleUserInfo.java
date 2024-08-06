package com.qnelldo.plantour.auth.dto;

import lombok.Data;

@Data
public class GoogleUserInfo {
    private String id;
    private String email;
    private boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;
}