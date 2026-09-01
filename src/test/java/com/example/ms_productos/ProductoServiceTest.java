package com.example.ms_productos;

import com.example.ms_productos.dto.ProductoDTO;
import com.example.ms_productos.dto.ProductoResponseDTO;
import com.example.ms_productos.exception.custom.DuplicateProductException;
import com.example.ms_productos.exception.custom.InsufficientStockException;
import com.example.ms_productos.exception.custom.ProductNotFoundException;
import com.example.ms_productos.model.Producto;
import com.example.ms_productos.repository.ProductoRepository;
import com.example.ms_productos.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    //getAllProductos

    @Test
    void deberiaRetornarListaDeProductos() {
        Producto producto = Producto.builder()
                .id(1L).nombre("Spider-Man").categoria("COMIC")
                .precio(14.99).stock(10).build();

        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<ProductoResponseDTO> resultado = productoService.getAllProductos();

        assertEquals(1, resultado.size());
        assertEquals("Spider-Man", resultado.get(0).getNombre());
        verify(productoRepository).findAll();
    }

    //getProductoById

    @Test
    void deberiaRetornarProductoPorId() {
        Producto producto = Producto.builder()
                .id(1L).nombre("Spider-Man").categoria("COMIC")
                .precio(14.99).stock(10).build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoResponseDTO resultado = productoService.getProductoById(1L);

        assertNotNull(resultado);
        assertEquals("Spider-Man", resultado.getNombre());
        verify(productoRepository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                productoService.getProductoById(99L));
    }

    //createProducto

    @Test
    void deberiaCrearProductoCorrectamente() {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Spider-Man");
        dto.setCategoria("COMIC");
        dto.setPrecio(14.99);
        dto.setStock(10);

        Producto saved = Producto.builder()
                .id(1L).nombre("Spider-Man").categoria("COMIC")
                .precio(14.99).stock(10).build();

        when(productoRepository.findByNombre("Spider-Man")).thenReturn(Optional.empty());
        when(productoRepository.save(any(Producto.class))).thenReturn(saved);

        ProductoResponseDTO resultado = productoService.createProducto(dto);

        assertNotNull(resultado);
        assertEquals("Spider-Man", resultado.getNombre());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoDuplicado() {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Spider-Man");
        dto.setCategoria("COMIC");
        dto.setPrecio(14.99);

        Producto existente = Producto.builder().id(1L).nombre("Spider-Man").build();
        when(productoRepository.findByNombre("Spider-Man")).thenReturn(Optional.of(existente));

        assertThrows(DuplicateProductException.class, () ->
                productoService.createProducto(dto));
    }

    //deleteProducto

    @Test
    void deberiaEliminarProductoCorrectamente() {
        Producto producto = Producto.builder()
                .id(1L).nombre("Spider-Man").build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.deleteProducto(1L);

        verify(productoRepository).delete(producto);
    }

    //pdateStock

    @Test
    void deberiaLanzarExcepcionCuandoStockInsuficiente() {
        Producto producto = Producto.builder()
                .id(1L).nombre("Spider-Man").stock(2).build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(InsufficientStockException.class, () ->
                productoService.updateStock(1L, 5));
    }

    @Test
    void deberiaActualizarStockCorrectamente() {
        Producto producto = Producto.builder()
                .id(1L).nombre("Spider-Man").stock(10).build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        productoService.updateStock(1L, 3);

        assertEquals(7, producto.getStock());
        verify(productoRepository).save(producto);
    }
}