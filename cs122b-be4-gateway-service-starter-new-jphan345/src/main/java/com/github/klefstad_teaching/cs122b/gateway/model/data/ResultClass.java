package com.github.klefstad_teaching.cs122b.gateway.model.data;

public class ResultClass {
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public ResultClass setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResultClass setMessage(String message) {
        this.message = message;
        return this;
    }
}
