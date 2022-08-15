package com.exossystem.task.utility;
import com.exossystem.task.exceptions.TokenValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Component
public class JwtUtility implements Serializable {
    static final long JWT_TOKEN_VALIDITY=5*60*60;
    @Value(("${jwt.secret}"))
    private String secretKey;
    public String getUsernameFromToken(String token)  {
        return getNameFromClaim(token);
    }
    public String getNameFromClaim(String token){
        final Claims claim = getAllClaimsFromToken(token);
        return claim.get("name").toString();
    }
    private Date getExpirationDateFromToken(String token)  {
        return getClaimFromToken(token,Claims::getExpiration);
    }
    private <T> T getClaimFromToken(String token, Function<Claims,T> claimResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token)  {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token)  {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    public String genrateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        claims.put("validated","true");
        claims.put("id","1234");
        claims.put("name",userDetails.getUsername());
        return doGenerateToken(claims);

    }
    private String doGenerateToken(Map<String,Object> claims){
        return Jwts.builder().setClaims(claims)
                .signWith(SignatureAlgorithm.HS512,secretKey).compact();
    }
    public Boolean validateToken(String token,UserDetails userDetails) throws TokenValidationException {
        final String username = getUsernameFromToken(token);
        boolean isExpired=false;//isTokenExpired(token);
        if(isExpired){
            throw new TokenValidationException("Token is expired");
        }
        return (username.equals(userDetails.getUsername()));
    }


    public Set<Map.Entry<String, Object>> getPayLoadData(String token) {
        //final Claims claim = getAllClaimsFromToken(token);
        return getClaimFromToken(token,Claims::entrySet);
    }
}
