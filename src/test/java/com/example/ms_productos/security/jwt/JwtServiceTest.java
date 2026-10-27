package com.example.ms_productos.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "una_clave_secreta_larga_y_segura_de_al_menos_32_bytes";

    private JwtService jwtService;
    private SecretKey clave;

    @BeforeEach
    void preparar() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        clave = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    private String tokenValido() {
        return Jwts.builder()
                .subject("ana")
                .claim("role", "ADMIN")
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(clave)
                .compact();
    }

    @Test
    void extractUsername_conTokenValido_devuelveElSubject() {
        assertEquals("ana", jwtService.extractUsername(tokenValido()));
    }

    @Test
    void extractUsername_conTokenMalFormado_devuelveNull() {
        assertNull(jwtService.extractUsername("esto-no-es-un-token"));
    }

    @Test
    void extractRole_conTokenValido_devuelveElRol() {
        assertEquals("ADMIN", jwtService.extractRole(tokenValido()));
    }

    @Test
    void extractRole_conTokenMalFormado_devuelveNull() {
        assertNull(jwtService.extractRole("esto-no-es-un-token"));
    }

    @Test
    void isTokenValid_conTokenVigente_devuelveTrue() {
        assertTrue(jwtService.isTokenValid(tokenValido()));
    }

    @Test
    void isTokenValid_conTokenVencido_devuelveFalse() {
        String vencido = Jwts.builder()
                .subject("ana")
                .expiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(clave)
                .compact();
        assertFalse(jwtService.isTokenValid(vencido));
    }

    @Test
    void isTokenValid_sinFechaDeExpiracion_devuelveFalse() {
        String sinExpiracion = Jwts.builder().subject("ana").signWith(clave).compact();
        assertFalse(jwtService.isTokenValid(sinExpiracion));
    }

    @Test
    void isTokenValid_firmadoConOtraClave_devuelveFalse() {
        SecretKey otraClave = Keys.hmacShaKeyFor("otra_clave_distinta_de_al_menos_32_bytes_ok".getBytes());
        String ajeno = Jwts.builder()
                .subject("ana")
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(otraClave)
                .compact();
        assertFalse(jwtService.isTokenValid(ajeno));
    }

    @Test
    void isTokenValid_conTextoBasura_devuelveFalse() {
        assertFalse(jwtService.isTokenValid("basura"));
    }
}