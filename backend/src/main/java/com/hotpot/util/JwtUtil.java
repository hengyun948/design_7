package com.hotpot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${hotpot.jwt.secret}")
    private String secret;

    @Value("${hotpot.jwt.expire}")
    private long expire;

    public String generateToken(Long id, String type) {
        return JWT.create()
                .withClaim("id", id)
                .withClaim("type", type)
                .withExpiresAt(new Date(System.currentTimeMillis() + expire))
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }

    public Long getId(String token) {
        return verify(token).getClaim("id").asLong();
    }

    public String getType(String token) {
        return verify(token).getClaim("type").asString();
    }
}
