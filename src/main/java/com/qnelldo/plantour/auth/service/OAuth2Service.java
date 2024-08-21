package com.qnelldo.plantour.auth.service;

import com.qnelldo.plantour.auth.dto.GoogleUserInfo;
import com.qnelldo.plantour.auth.dto.TokenResponse;
import com.qnelldo.plantour.user.dto.UserDTO;
import com.qnelldo.plantour.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuth2Service {


    private RestTemplate restTemplate;
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(OAuth2Service.class);

    @Autowired
    public OAuth2Service(UserService userService) {
        this.restTemplate = new RestTemplate();
        this.userService = userService;
    }


    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String redirectUri;


    public String getAccessToken(String code) {
        String url = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        logger.info("Sending request to Google with params: {}", params);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(url, request, TokenResponse.class);
            logger.info("Received response from Google: {}", response);
            return response.getBody().getAccess_token();
        } catch (Exception e) {
            logger.error("Error while getting access token from Google", e);
            throw e;
        }
    }

    public UserDTO getUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GoogleUserInfo.class
        );

        GoogleUserInfo userInfo = response.getBody();
        return userService.processOAuthUser(userInfo.getEmail(), userInfo.getName(), userInfo.getPicture(), userInfo.getId());
    }
}