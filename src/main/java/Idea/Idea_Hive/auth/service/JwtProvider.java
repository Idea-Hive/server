package Idea.Idea_Hive.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey SECRET_KEY;
    @Getter
    private final Long accessTokenValidityInMillisecnds;
    @Getter
    private final Long refreshTokenValidityInMilliseconds;

    public JwtProvider(
            @Value("${spring.jwt.key}") final String secretKey,
            @Value("${spring.jwt.validity.atk}") final Long accessTokenValidityInMillisecnds,
            @Value("${spring.jwt.validity.rtk}") final Long refreshTokenValidityInMilliseconds) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMillisecnds = accessTokenValidityInMillisecnds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    public String createAccessToken(final String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInMillisecnds);
        return Jwts.builder()
                .setSubject(email)
                .setIssuer("Taskmate")
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim("type", "access")
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(final String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.refreshTokenValidityInMilliseconds);
        return Jwts.builder()
                .setSubject(email)
                .setIssuer("Taskmate")
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim("type", "refresh")
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(final String jwt) {
        try {
            parseToken(jwt);
        } catch (final ExpiredJwtException e) {
            throw new BadCredentialsException("만료된 토큰입니다.", e);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("잘못된 토큰입니다.", e);
        }
    }

    public Jws<Claims> parseToken(final String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwt);
    }

    public String getEmail(String jwt) {
        return parseToken(jwt)
                .getBody()
                .getSubject();
    }

    public Claims getClaims(String jwt) {
        return parseToken(jwt).getBody();
    }

}
