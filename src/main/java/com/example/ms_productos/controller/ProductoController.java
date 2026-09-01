package com.example.ms_productos.controller;

import com.example.ms_productos.dto.ProductoDTO;
import com.example.ms_productos.dto.ProductoResponseDTO;
import com.example.ms_productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Operaciones sobre productos")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;

    @Operation(summary = "Obtener todos los productos", description = "Retorna la lista completa de productos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<?> getAllProductos() {
        log.debug("Solicitando todos los productos");
        List<ProductoResponseDTO> productos = productoService.getAllProductos();
        productos.forEach(p ->
                p.add(linkTo(methodOn(ProductoController.class).getProductoById(p.getId())).withSelfRel())
        );
        return ResponseEntity.ok(Map.of(
                "productos", productos,
                "total", productos.size()
        ));
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductoById(@PathVariable Long id) {
        log.debug("Solicitando producto por ID: {}", id);
        ProductoResponseDTO producto = productoService.getProductoById(id);
        producto.add(linkTo(methodOn(ProductoController.class).getProductoById(id)).withSelfRel());
        producto.add(linkTo(methodOn(ProductoController.class).getAllProductos()).withRel("todos"));
        producto.add(linkTo(methodOn(ProductoController.class).updateProducto(id, null)).withRel("actualizar"));
        producto.add(linkTo(methodOn(ProductoController.class).deleteProducto(id)).withRel("eliminar"));
        return ResponseEntity.ok(producto);
    }

    @Operation(summary = "Crear producto", description = "Crea un nuevo producto (solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        log.info("ADMIN - Creando nuevo producto");
        ProductoResponseDTO producto = productoService.createProducto(productoDTO);
        producto.add(linkTo(methodOn(ProductoController.class).getProductoById(producto.getId())).withSelfRel());
        producto.add(linkTo(methodOn(ProductoController.class).getAllProductos()).withRel("todos"));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Producto creado correctamente",
                "producto", producto
        ));
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente (solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        log.info("ADMIN - Actualizando producto ID: {}", id);
        ProductoResponseDTO producto = productoService.updateProducto(id, productoDTO);
        producto.add(linkTo(methodOn(ProductoController.class).getProductoById(id)).withSelfRel());
        producto.add(linkTo(methodOn(ProductoController.class).getAllProductos()).withRel("todos"));
        producto.add(linkTo(methodOn(ProductoController.class).deleteProducto(id)).withRel("eliminar"));
        return ResponseEntity.ok(Map.of(
                "message", "Producto actualizado correctamente",
                "producto", producto
        ));
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID (solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        log.warn("ADMIN - Eliminando producto ID: {}", id);
        productoService.deleteProducto(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado correctamente"));
    }

    @Operation(summary = "Actualizar stock", description = "Descuenta stock de un producto (solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente")
    })
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer quantity = body.get("quantity");
        log.info("ADMIN - Actualizando stock - Producto ID: {}, Cantidad a descontar: {}", id, quantity);
        productoService.updateStock(id, quantity);
        return ResponseEntity.ok(Map.of("message", "Stock actualizado correctamente"));
    }

    @Operation(summary = "Verificar existencia de producto", description = "Verifica si un producto existe por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verificación exitosa")
    })
    @GetMapping("/exists/{id}")
    public ResponseEntity<?> productExists(@PathVariable Long id) {
        log.debug("Verificando existencia de producto - ID: {}", id);
        boolean exists = productoService.productExists(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}