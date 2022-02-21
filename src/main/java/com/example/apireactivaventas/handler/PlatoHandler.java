package com.example.apireactivaventas.handler;

import com.example.apireactivaventas.model.Plato;
import com.example.apireactivaventas.service.IPlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class PlatoHandler {

    @Autowired
    private IPlatoService service;

    public Mono<ServerResponse> listar(ServerRequest req) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.listar(), Plato.class);
    }

    public Mono<ServerResponse> listarPorId(ServerRequest req) {
        return service.listarPorId(req.pathVariable("id"))
                .flatMap( p -> ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> registrar(ServerRequest req) {
        Mono<Plato> monoPlato = req.bodyToMono(Plato.class);
        return monoPlato
                .flatMap( p -> service.registrar(p))
                .flatMap( p -> ServerResponse
                                .created(URI.create(req.uri().toString().concat(p.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(p))
                );
    }

    public Mono<ServerResponse> modificar(ServerRequest req) {
        Mono<Plato> monoPlato = req.bodyToMono(Plato.class);
        String id = req.pathVariable("id");
        return service.listarPorId(id)
                .zipWith(monoPlato, (bd, pl) -> {
                    bd.setId(id);
                    bd.setNombre(pl.getNombre());
                    bd.setPrecio(pl.getPrecio());
                    bd.setEstado(pl.getEstado());
                    return bd;
                })
                .flatMap(service::modificar)
                .flatMap( p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest req) {
        return service.listarPorId(req.pathVariable("id"))
                .flatMap( p -> service.eliminar(p.getId()))
                .flatMap( p -> ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
