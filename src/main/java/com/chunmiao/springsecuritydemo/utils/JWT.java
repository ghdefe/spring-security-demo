package com.chunmiao.springsecuritydemo.utils;

/**
 * 提供一个JWT类，构造该类时，只需要将想放在token上的信息传入构造函数，即可得到一个想要的JWT，调用toString方法就得到了token
 */
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
