package com.hmdp.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

public class JwtHelper {

    // token过期时间
    private static long tokenExpiration = 24 * 60 * 60 * 1000;

    // 签名密钥
    private static String tokenSignKey = "123456";

    // 根据参数生成token

    /**
     * JWT会结合公共部分(自定义)、私有部分(如id、name)、和签名部分(自定义私钥)生成token
     */
    public static String createToken(Long userId, String phone) {
        String token = Jwts.builder()
                // 分类（公共部分）
                .setSubject("HMDP")
                // 设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                // 私有部分主题信息
                .claim("userId", userId)
                .claim("phone", phone)
                // 设置私钥
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    // 根据token字符串得到用户id
    public static Long getUserId(String token) {
        if (StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
    }

    // 根据token字符串得到用户手机号
    public static String getUserName(String token) {
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("phone");
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L, "lucy");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUserName(token));
    }
}