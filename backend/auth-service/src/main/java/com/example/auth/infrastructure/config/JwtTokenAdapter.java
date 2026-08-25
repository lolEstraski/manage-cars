package com.example.auth.infrastructure.config;

import com.example.auth.domain.model.User;
import com.example.auth.domain.ports.out.TokenPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenAdapter implements TokenPort {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Override
    public String generarToken(User usuario) {
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("userId", usuario.getId())
                .claim("rol", usuario.getRol().getNombre())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
