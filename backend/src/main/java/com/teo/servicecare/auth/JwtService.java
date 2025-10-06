package com.teo.servicecare.auth;

import com.teo.servicecare.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {
  private final JwtProperties props;

  public JwtService(JwtProperties props) {
    this.props = props;
  }

  public String generateToken(String email) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + props.getExpirationMs());
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(Keys.hmacShaKeyFor(props.getSecret().getBytes()), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractEmail(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(props.getSecret().getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean isTokenValid(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(Keys.hmacShaKeyFor(props.getSecret().getBytes()))
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
