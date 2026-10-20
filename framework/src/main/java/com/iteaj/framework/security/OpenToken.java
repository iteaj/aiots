package com.iteaj.framework.security;

public class OpenToken extends SecurityToken{

    public OpenToken(String token, String account) {
        this.setCode(token);
        this.setAccount(account);
    }

    public String getToken() {
        return this.getCode();
    }
}
