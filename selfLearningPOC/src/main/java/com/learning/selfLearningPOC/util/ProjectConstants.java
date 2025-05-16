package com.learning.selfLearningPOC.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProjectConstants {
    public static String JWT_SECURITY_SECRET;
    public static final String JWT_HEADER = "Authorization";


    @Value("${jwt.security.secret}")
    public void setJwtSecuritySecret(String jwtSecuritySecret) {
        JWT_SECURITY_SECRET = jwtSecuritySecret;
    }

}
