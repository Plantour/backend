package com.qnelldo.plantour.user.dto;

import lombok.Data;
import java.util.Map;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String profilePicture;
    private Map<String, String> nickname;  // 다국어 지원을 위해 Map으로 변경
    private String languageCode;
}