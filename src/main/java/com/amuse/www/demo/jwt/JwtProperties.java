package com.amuse.www.demo.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.SignatureAlgorithm;


/*
    jwt 정보를 저장하는 class
    jwt 실제 정보는 application.yml 파일에 있음
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private SignatureAlgorithm signatureAlgorithm;
    private Long tokenExpired;
}