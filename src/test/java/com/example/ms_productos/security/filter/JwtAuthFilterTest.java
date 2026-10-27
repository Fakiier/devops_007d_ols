package com.example.ms_productos.security.filter;

import com.example.ms_productos.security.jwt.JwtService;
import com.example.ms_productos.service.CustomUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    private JwtAuthFilter filtro;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain cadena;

    @BeforeEach
    void preparar() {
        filtro = new JwtAuthFilter(jwtService, new CustomUserDetailsService());
        request = new MockHttpServletRequest("GET", "/api/v1/productos");
        response = new MockHttpServletResponse();
        cadena = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void sinCabeceraAuthorization_noAutenticaYContinuaLaCadena() throws Exception {
        filtro.doFilter(request, response, cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
    }

    @Test
    void cabeceraSinBearer_noAutenticaYContinuaLaCadena() throws Exception {
        request.addHeader("Authorization", "Basic abc123");

        filtro.doFilter(request, response, cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
    }

    @Test
    void tokenValido_estableceLaAutenticacionConSuRol() throws Exception {
        request.addHeader("Authorization", "Bearer token-ok");
        when(jwtService.isTokenValid("token-ok")).thenReturn(true);
        when(jwtService.extractUsername("token-ok")).thenReturn("ana");
        when(jwtService.extractRole("token-ok")).thenReturn("ADMIN");

        filtro.doFilter(request, response, cadena);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("ana", auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertNotNull(cadena.getRequest());
    }

    @Test
    void tokenInvalido_noAutenticaYContinuaLaCadena() throws Exception {
        request.addHeader("Authorization", "Bearer token-malo");
        when(jwtService.isTokenValid("token-malo")).thenReturn(false);

        filtro.doFilter(request, response, cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
    }

    @Test
    void errorAlProcesarElToken_noRompeLaCadena() throws Exception {
        request.addHeader("Authorization", "Bearer token-roto");
        when(jwtService.isTokenValid("token-roto")).thenThrow(new RuntimeException("fallo"));

        filtro.doFilter(request, response, cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
    }
}