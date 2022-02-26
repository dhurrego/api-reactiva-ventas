package com.example.apireactivaventas;

import com.example.apireactivaventas.model.Plato;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiReactivaVentasApplicationTests {

    @Autowired
    private WebTestClient cliente;
//
//    @Test
//    void probarPlatosListar() {
//        cliente.get()
//                .uri("/platos")
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBodyList(Plato.class);
//    }
//
//    @Test
//    void probarPlatosRegistrar() {
//        Plato plato = new Plato();
//        plato.setNombre("Salchipapa sencilla");
//        plato.setEstado(true);
//        plato.setPrecio(9500);
//        cliente.post()
//                .uri("/platos")
//                .body(Mono.just(plato), Plato.class)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody()
//                .jsonPath("$.nombre").isNotEmpty()
//                .jsonPath("$.precio").isNumber();
//    }
//
//    @Test
//    void probarPlatosModificar() {
//        Plato plato = new Plato();
//        plato.setNombre("Perro caliente sencillo");
//        plato.setEstado(true);
//        plato.setPrecio(12000);
//        cliente.put()
//                .uri("/platos/".concat("62106d0b9c98ab09ec02ea49"))
//                .body(Mono.just(plato), Plato.class)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody()
//                .jsonPath("$.nombre").isNotEmpty()
//                .jsonPath("$.precio").isNumber();
//    }
//
//    @Test
//    void probarPlatosEliminar() {
//        cliente.delete()
//                .uri("/platos/".concat("1234"))
//                .exchange()
//                .expectStatus().isNotFound();
//    }

}
