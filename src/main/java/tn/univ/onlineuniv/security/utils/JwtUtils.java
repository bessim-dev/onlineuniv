package tn.univ.onlineuniv.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.userdetails.User;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public class JwtUtils {
    private final Algorithm algorithm = Algorithm.HMAC256("bessim".getBytes());



    public Date expiresAt(int min){
        return new Date(System.currentTimeMillis() + (long) min *60*1000);
    }
    public String createAccessToken(User user , List<String> authorities, HttpServletRequest request){
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expiresAt(10))
                .withIssuer(request.getRequestURI().toString())
                .withClaim("roles",authorities)
                .sign(algorithm);
    }
    public String createAccessToken(tn.univ.onlineuniv.models.User user , List<String> authorities, HttpServletRequest request){
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expiresAt(10))
                .withIssuer(request.getRequestURI().toString())
                .withClaim("roles",authorities)
                .sign(algorithm);
    }
    public String createRefreshToken (User user, HttpServletRequest request){
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expiresAt(100))
                .withIssuer(request.getRequestURI().toString())
                .sign(algorithm);
    }
    public DecodedJWT decodeTokens(String token){
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
