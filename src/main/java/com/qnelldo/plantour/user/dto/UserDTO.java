package com.qnelldo.plantour.user.dto;

import lombok.Data;
import java.util.Map;

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