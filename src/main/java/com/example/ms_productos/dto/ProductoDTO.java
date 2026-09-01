package com.example.ms_productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Datos para crear o actualizar un producto")
public class ProductoDTO {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre debe tener máximo 100 caracteres")
    @Schema(description = "Nombre del producto", example = "Spider-Man No Way Home", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "El tipo es requerido")
    @Pattern(regexp = "GAME|COMIC", message = "El tipo debe ser GAME o COMIC")
    @Schema(description = "Categoría del producto", example = "COMIC", allowableValues = {"GAME", "COMIC"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;

    @NotNull(message = "El precio es requerido")
    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio del producto", example = "14.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precio;

    @Schema(description = "Descripción del producto", example = "Cómic edición especial")
    private String descripcion;

    //@NotBlank(message = "La URL de la imagen es requerida")
    //private String imagenUrl;

    @PositiveOrZero(message = "El stock debe ser mayor o igual a 0")
    @Schema(description = "Stock disponible del producto", example = "50")
    private Integer stock;
}
