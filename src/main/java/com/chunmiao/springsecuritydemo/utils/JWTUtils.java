package com.chunmiao.springsecuritydemo.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 提供信息HMACSHA256加密、信息签名、测试token是否合法
 */
public class JWTUtils {
    public static final String DEFAULT_HEADER = "\"alg\":\"HS256\",\"typ\":\"JWT\"";

    public static final String SECRET = "woshizengchunmiao";

    public static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    public static final String HEADER_TOKEN_NAME = "Authrization";

    public static String encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String decode(String input) {
        return new String(Base64.getDecoder().decode(input));
    }

    public static String HMACSHA256(String data, String secret) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] bytes = hmacSHA256.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toHexString((aByte & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString().toUpperCase();
    }

    public static String getSignature(String payload) throws Exception {
        return HMACSHA256(encode(DEFAULT_HEADER) + encode(payload), SECRET);
    }

    public static String testJwt(String jwt) {
        String[] split = jwt.split("\\.");
        try {
            if (!(HMACSHA256(split[0] + split[1], SECRET).equals(split[2]))) {
                return null;
            }
            if (!decode(split[0]).equals(DEFAULT_HEADER)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decode(split[1]);
    }
}
