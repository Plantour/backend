package com.qnelldo.plantour.user.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String profilePicture;
    private String nickname;
    private String customNickname;
    private String languageCode;
}