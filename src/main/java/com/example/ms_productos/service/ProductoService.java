package com.example.ms_productos.service;

import com.example.ms_productos.dto.ProductoDTO;
import com.example.ms_productos.dto.ProductoResponseDTO;
import com.example.ms_productos.exception.custom.*;
import com.example.ms_productos.model.Producto;
import com.example.ms_productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    public Producto findByIdOrThrow(Long id) {
        log.debug("Buscando producto por ID: {}", id);
        return productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado - ID: {}", id);
                    return new ProductNotFoundException("Producto no encontrado con ID: " + id);
                });
    }

    public List<ProductoResponseDTO> getAllProductos() {
        log.debug("Obteniendo todos los productos");
        List<ProductoResponseDTO> productos = productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.debug("Total de productos encontrados: {}", productos.size());
        return productos;
    }

    public ProductoResponseDTO getProductoById(Long id) {
        log.debug("Obteniendo producto por ID: {}", id);
        Producto producto = findByIdOrThrow(id);
        return convertToDTO(producto);
    }


    @Transactional
    public ProductoResponseDTO createProducto(ProductoDTO productoDTO) {
        log.info("Creando nuevo producto - Nombre: {}, Tipo: {}", productoDTO.getNombre(), productoDTO.getCategoria());

        if (productoRepository.findByNombre(productoDTO.getNombre()).isPresent()) {
            log.warn("Producto ya existe con nombre: {}", productoDTO.getNombre());
            throw new DuplicateProductException("Ya existe un producto con el nombre: " + productoDTO.getNombre());
        }

        Producto producto = Producto.builder()
                .nombre(productoDTO.getNombre())
                .categoria(productoDTO.getCategoria())
                .precio(productoDTO.getPrecio())
                .descripcion(productoDTO.getDescripcion())
                //.imagenUrl(productoDTO.getImagenUrl())
                .stock(productoDTO.getStock() != null ? productoDTO.getStock() : 0)
                .build();

        Producto savedProducto = productoRepository.save(producto);
        log.info("Producto creado exitosamente - ID: {}, Nombre: {}", savedProducto.getId(), savedProducto.getNombre());
        return convertToDTO(savedProducto);
    }

    @Transactional
    public ProductoResponseDTO updateProducto(Long id, ProductoDTO productoDTO) {
        log.info("Actualizando producto - ID: {}", id);
        Producto producto = findByIdOrThrow(id);

        if (productoDTO.getNombre() != null && !productoDTO.getNombre().isBlank()) {
            productoRepository.findByNombre(productoDTO.getNombre()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    log.warn("Nombre ya en uso - ID: {}, Nombre: {}", id, productoDTO.getNombre());
                    throw new DuplicateProductException("Ya existe un producto con el nombre: " + productoDTO.getNombre());
                }
            });
            producto.setNombre(productoDTO.getNombre());
        }

        if (productoDTO.getCategoria() != null) {
            if (!productoDTO.getCategoria().equals("GAME") && !productoDTO.getCategoria().equals("COMIC")) {
                throw new InvalidProductDataException("Tipo inválido. Use: GAME o COMIC");
            }
            producto.setCategoria(productoDTO.getCategoria());
        }

        if (productoDTO.getPrecio() != null) {
            if (productoDTO.getPrecio() <= 0) {
                throw new InvalidProductDataException("El precio debe ser mayor a 0");
            }
            producto.setPrecio(productoDTO.getPrecio());
        }

        if (productoDTO.getDescripcion() != null) {
            producto.setDescripcion(productoDTO.getDescripcion());
        }

        //if (productoDTO.getImagenUrl() != null) {
            //producto.setImagenUrl(productoDTO.getImagenUrl());
        //}

        if (productoDTO.getStock() != null) {
            if (productoDTO.getStock() < 0) {
                throw new InvalidProductDataException("El stock no puede ser negativo");
            }
            producto.setStock(productoDTO.getStock());
        }

        Producto savedProducto = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente - ID: {}", id);
        return convertToDTO(savedProducto);
    }

    @Transactional
    public void deleteProducto(Long id) {
        log.warn("Eliminando producto - ID: {}", id);
        Producto producto = findByIdOrThrow(id);
        productoRepository.delete(producto);
        log.info("Producto eliminado exitosamente - ID: {}, Nombre: {}", id, producto.getNombre());
    }

    @Transactional
    public void updateStock(Long id, Integer quantity) {
        log.info("Actualizando stock - Producto ID: {}, Cantidad a descontar: {}", id, quantity);
        Producto producto = findByIdOrThrow(id);

        if (quantity < 0) {
            throw new InvalidProductDataException("La cantidad a descontar no puede ser negativa");
        }

        if (producto.getStock() < quantity) {
            log.warn("Stock insuficiente - Producto: {}, Stock actual: {}, Requerido: {}",
                    producto.getNombre(), producto.getStock(), quantity);
            throw new InsufficientStockException("Stock insuficiente para: " + producto.getNombre() +
                    ". Disponible: " + producto.getStock());
        }

        producto.setStock(producto.getStock() - quantity);
        productoRepository.save(producto);
        log.info("Stock actualizado - Producto: {}, Nuevo stock: {}", producto.getNombre(), producto.getStock());
    }

    public boolean productExists(Long id) {
        log.debug("Verificando existencia de producto - ID: {}", id);
        boolean exists = productoRepository.existsById(id);
        log.debug("Producto ID: {} - Existe: {}", id, exists);
        return exists;
    }

    private ProductoResponseDTO convertToDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .categoria(producto.getCategoria())
                .precio(producto.getPrecio())
                .descripcion(producto.getDescripcion())
                //.imagenUrl(producto.getImagenUrl())
                .stock(producto.getStock())
                .build();
    }
}
