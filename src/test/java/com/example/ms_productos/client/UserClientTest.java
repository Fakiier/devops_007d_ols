package com.example.ms_productos.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserClientTest {

    private UserClient clienteCon(ExchangeFunction respuesta) {
        return new UserClient(WebClient.builder().exchangeFunction(respuesta));
    }

    private ExchangeFunction responderJson(String json) {
        return request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .build());
    }

    @Test
    void userExists_cuandoMsUsersResponderTrue_devuelveTrue() {
        assertTrue(clienteCon(responderJson("{\"exists\":true}")).userExists("ana"));
    }

    @Test
    void userExists_cuandoMsUsersResponderFalse_devuelveFalse() {
        assertFalse(clienteCon(responderJson("{\"exists\":false}")).userExists("ana"));
    }

    @Test
    void userExists_sinElCampoExists_devuelveFalse() {
        assertFalse(clienteCon(responderJson("{}")).userExists("ana"));
    }

    @Test
    void userExists_conRespuestaSinCuerpo_devuelveFalse() {
        ExchangeFunction vacia = request -> Mono.just(ClientResponse.create(HttpStatus.OK).build());
        assertFalse(clienteCon(vacia).userExists("ana"));
    }

    @Test
    void userExists_cuandoMsUsersFallaConError500_devuelveFalse() {
        ExchangeFunction error500 = request ->
                Mono.just(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build());
        assertFalse(clienteCon(error500).userExists("ana"));
    }

    @Test
    void userExists_cuandoNoHayConexion_devuelveFalse() {
        ExchangeFunction caido = request -> Mono.error(new RuntimeException("servicio caido"));
        assertFalse(clienteCon(caido).userExists("ana"));
    }
}