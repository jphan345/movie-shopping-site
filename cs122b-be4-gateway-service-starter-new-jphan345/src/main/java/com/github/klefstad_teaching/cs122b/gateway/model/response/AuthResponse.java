package com.github.klefstad_teaching.cs122b.gateway.model.response;
import com.github.klefstad_teaching.cs122b.gateway.model.data.ResultClass;

public class AuthResponse {
    private ResultClass result;

    public ResultClass getResult() {
        return result;
    }

    public AuthResponse setResult(ResultClass result) {
        this.result = result;
        return this;
    }
}
