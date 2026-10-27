package com.example.ms_productos.exception;

import com.example.ms_productos.exception.custom.*;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @SuppressWarnings("unused")
    private void metodoDummy(String parametro) { }

    @Test
    void validacion_devuelve400ConLosCamposInvalidos() throws Exception {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(new Object(), "producto");
        result.addError(new FieldError("producto", "nombre", "no puede estar vacio"));
        MethodParameter parametro = new MethodParameter(
                getClass().getDeclaredMethod("metodoDummy", String.class), 0);

        ErrorResponse respuesta = handler.handleValidationExceptions(
                new MethodArgumentNotValidException(parametro, result));

        assertEquals(400, respuesta.getStatus());
        assertEquals("no puede estar vacio", respuesta.getErrors().get("nombre"));
    }

    @Test
    void productoNoEncontrado_devuelve404() {
        ResponseEntity<ErrorResponse> r = handler.handleProductNotFound(new ProductNotFoundException("no existe"));
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("no existe", r.getBody().getMessage());
    }

    @Test
    void datosInvalidos_devuelve400() {
        ResponseEntity<ErrorResponse> r = handler.handleInvalidProductData(new InvalidProductDataException("datos malos"));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
    }

    @Test
    void stockInsuficiente_devuelve409() {
        ResponseEntity<ErrorResponse> r = handler.handleInsufficientStock(new InsufficientStockException("sin stock"));
        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
    }

    @Test
    void productoDuplicado_devuelve409() {
        ResponseEntity<ErrorResponse> r = handler.handleDuplicateProduct(new DuplicateProductException("repetido"));
        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
    }

    @Test
    void accesoDenegado_devuelve403() {
        ResponseEntity<ErrorResponse> r = handler.handleAccessDenied(new AccessDeniedException("no"));
        assertEquals(HttpStatus.FORBIDDEN, r.getStatusCode());
    }

    @Test
    void argumentoInvalido_devuelve400() {
        ResponseEntity<ErrorResponse> r = handler.handleIllegalArgument(new IllegalArgumentException("mal"));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
    }

    @Test
    void errorEnEjecucion_devuelve500ConElMensaje() {
        ResponseEntity<ErrorResponse> r = handler.handleRuntimeException(new RuntimeException("fallo"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertEquals("fallo", r.getBody().getMessage());
    }

    @Test
    void errorGenerico_devuelve500ConMensajeGenerico() {
        ResponseEntity<ErrorResponse> r = handler.handleGenericException(new Exception("interno"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertEquals("Ha ocurrido un error inesperado", r.getBody().getMessage());
    }
}