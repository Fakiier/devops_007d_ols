package com.example.ms_productos.controller;

import com.example.ms_productos.dto.ProductoResponseDTO;
import com.example.ms_productos.security.jwt.JwtService;
import com.example.ms_productos.service.CustomUserDetailsService;
import com.example.ms_productos.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    //getAllProductos

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deberiaRetornarTodosLosProductos() throws Exception {
        ProductoResponseDTO producto = ProductoResponseDTO.builder()
                .id(1L).nombre("Spider-Man").categoria("COMIC")
                .precio(14.99).stock(10).build();

        when(productoService.getAllProductos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.productos[0].nombre").value("Spider-Man"));
    }

    //getProductoById

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deberiaRetornarProductoPorId() throws Exception {
        ProductoResponseDTO producto = ProductoResponseDTO.builder()
                .id(1L).nombre("Spider-Man").categoria("COMIC")
                .precio(14.99).stock(10).build();

        when(productoService.getProductoById(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Spider-Man"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.todos").exists());
    }

    //crreateProducto

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deberiaCrearProducto() throws Exception {
        ProductoResponseDTO producto = ProductoResponseDTO.builder()
                .id(1L).nombre("Spider-Man").categoria("COMIC")
                .precio(14.99).stock(10).build();

        when(productoService.createProducto(any())).thenReturn(producto);

        String json = """
                {
                    "nombre": "Spider-Man",
                    "categoria": "COMIC",
                    "precio": 14.99,
                    "stock": 10
                }
                """;

        mockMvc.perform(post("/api/v1/productos")
                        .contentType("application/json")
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Producto creado correctamente"))
                .andExpect(jsonPath("$.producto.nombre").value("Spider-Man"));
    }

    //updateProducto

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deberiaActualizarProducto() throws Exception {
        ProductoResponseDTO producto = ProductoResponseDTO.builder()
                .id(1L).nombre("Spider-Man Updated").categoria("COMIC")
                .precio(19.99).stock(5).build();

        when(productoService.updateProducto(anyLong(), any())).thenReturn(producto);

        String json = """
                {
                    "nombre": "Spider-Man Updated",
                    "categoria": "COMIC",
                    "precio": 19.99,
                    "stock": 5
                }
                """;

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType("application/json")
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto actualizado correctamente"))
                .andExpect(jsonPath("$.producto.nombre").value("Spider-Man Updated"));
    }

    // deleteProducto

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deberiaEliminarProducto() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto eliminado correctamente"));
    }

    //productExists

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deberiaVerificarExistenciaDeProducto() throws Exception {
        when(productoService.productExists(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/productos/exists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }
}