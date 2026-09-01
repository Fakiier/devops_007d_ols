package com.example.ms_productos.repository;

import com.example.ms_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombre(String nombre);

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByStockLessThan(Integer stock);
}
