package com.github.klefstad_teaching.cs122b.idm.model.response;

public class RefreshRequest {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public RefreshRequest setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
