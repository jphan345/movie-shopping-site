package com.github.klefstad_teaching.cs122b.gateway.model.response;

public class AuthRequest {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public AuthRequest setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
