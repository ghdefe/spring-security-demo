package com.chunmiao.springsecuritydemo.utils;

public class JWT {
    private String header;

    private String payload;

    private String signature;

    public JWT(String payload) throws Exception {
        this.payload = JWTUtils.encode(payload);
        this.header = JWTUtils.encode(JWTUtils.DEFAULT_HEADER);
        this.signature = JWTUtils.getSignature(payload);
    }

    @Override
    public String toString() {
        return header + "." + payload + "." + signature;
    }
}
