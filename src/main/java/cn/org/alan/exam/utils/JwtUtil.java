package cn.org.alan.exam.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Component
@Slf4j
@SuppressWarnings("all")
public class JwtUtil {

    
    @Value("${jwt.secret}")
    private String secret;

    
    @Value("${jwt.expiration}")
    private long expiration;

    
    @Value("${jwt.refreshThreshold}")
    private long refreshThreshold;

    
    public String createJwt(String userInfo, List<String> authList) {
        Date issDate = new Date();
        Date expireDate = new Date(issDate.getTime() + expiration);
        
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256"); 
        headerClaims.put("typ", "JWT"); 
        return JWT.create().withHeader(headerClaims)
                .withIssuer("wj") 
                .withIssuedAt(issDate) 
                .withExpiresAt(expireDate) 
                .withClaim("userInfo", userInfo) 
                .withClaim("authList", authList)
                .sign(Algorithm.HMAC256(secret)); 
    }

    
    private JWTVerifier getVerifier() {
        
        
        
        
        return JWT.require(Algorithm.HMAC256(secret)).build();
    }
    
    public String verifyAndRefreshToken(String token, String storedToken) {
        
        if (StringUtils.isBlank(storedToken) || !token.equals(storedToken)) {
            return null;
        }
        
        JWTVerifier verifier = getVerifier();
        try {
            DecodedJWT jwt = verifier.verify(token);
            
            if (shouldRefresh(jwt)) {
                log.info("Token即将过期，开始续签。过期时间：{}，当前时间：{}", 
                    jwt.getExpiresAt(), new Date());
                String userInfo = jwt.getClaim("userInfo").asString();
                List<String> authList = jwt.getClaim("authList").asList(String.class);
                return createJwt(userInfo, authList);
            }
            return token;
        } catch (JWTVerificationException e) {
            log.error("Token验证失败", e);
            return null;
        }
    }

    
    private boolean shouldRefresh(DecodedJWT jwt) {
        Date expirationDate = jwt.getExpiresAt();
        long currentTime = System.currentTimeMillis();
        long remainingTime = expirationDate.getTime() - currentTime;
        log.info("Token剩余时间：{}ms，续签阈值：{}ms", remainingTime, refreshThreshold);
        return remainingTime < refreshThreshold;
    }

    
    public boolean verifyToken(String token) {
        
        JWTVerifier verifier = getVerifier();
        try {

            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("校验失败");
            return false;
        }
    }

    
    public String getUser(String token) {
        
        JWTVerifier verifier = getVerifier();
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("userInfo").asString();
        } catch (JWTVerificationException e) {
            log.error("用户获取失败");
            return null;
        }
    }

    
    public List<String> getAuthList(String token) {
        
        JWTVerifier verifier = getVerifier();
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("authList").asList(String.class);
        } catch (JWTVerificationException e) {
            log.error("权限列表+获取失败");
            return null;
        }
    }

}
