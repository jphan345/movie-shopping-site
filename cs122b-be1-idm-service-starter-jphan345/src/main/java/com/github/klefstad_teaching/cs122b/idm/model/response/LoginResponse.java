package com.github.klefstad_teaching.cs122b.idm.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class LoginResponse extends ResponseModel<LoginResponse> {
    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public LoginResponse setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LoginResponse setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
