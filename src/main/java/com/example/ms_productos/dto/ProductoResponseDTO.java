package com.example.ms_productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con los datos de un producto")
public class ProductoResponseDTO extends RepresentationModel<ProductoResponseDTO> {

    @Schema(description = "ID del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del producto", example = "Spider-man No Way Home")
    private String nombre;

    @Schema(description = "Categoría del producto", example = "COMIC")
    private String categoria;

    @Schema(description = "Precio del producto", example = "14.99")
    private Double precio;

    @Schema(description = "Descripción del producto", example = "Cómic edición limitada")
    private String descripcion;
    //private String imagenUrl;

    @Schema(description = "Stock disponible", example = "50", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer stock;
}