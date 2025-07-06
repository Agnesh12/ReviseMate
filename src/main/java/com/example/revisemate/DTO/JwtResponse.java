package com.example.revisemate.DTO;

import com.example.revisemate.Model.User;

public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private User user;

    public JwtResponse(String accessToken, String refreshToken, User user) {
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
        this.user         = user;
    }



    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
