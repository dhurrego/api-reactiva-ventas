package com.example.apireactivaventas;

import com.example.apireactivaventas.handler.PlatoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> rutasPlato(PlatoHandler handler) {
        return route(GET("/v2/platos"), handler::listar)
                .andRoute(GET("/v2/platos/{id}"), handler::listarPorId)
                .andRoute(POST("/v2/platos"), handler::registrar)
                .andRoute(PUT("/v2/platos/{id}"), handler::modificar)
                .andRoute(DELETE("/v2/platos/{id}"), handler::eliminar);
    }
}
