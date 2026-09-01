package com.example.ms_productos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private String categoria; // "GAME" o "COMIC"

    @Column(nullable = false)
    private Double precio;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    //@Column(nullable = false)
    //private String imagenUrl;

    private Integer stock;
}