package com.example.apireactivaventas.handler;

import com.example.apireactivaventas.model.Cliente;
import com.example.apireactivaventas.service.IClienteService;
import com.example.apireactivaventas.validators.RequestValidators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ClienteHandler {

    @Autowired
    private IClienteService service;

    @Autowired
    private RequestValidators validadorGeneral;

    public Mono<ServerResponse> listar(ServerRequest req) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.listar(), Cliente.class);
    }

    public Mono<ServerResponse> listarPorId(ServerRequest req) {
        return service.listarPorId(req.pathVariable("id"))
                .flatMap( c -> ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(c)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> registrar(ServerRequest req) {
        Mono<Cliente> monoCliente = req.bodyToMono(Cliente.class);
        return monoCliente
                .flatMap(validadorGeneral::validate)
                .flatMap( c -> service.registrar(c))
                .flatMap( c -> ServerResponse
                                .created(URI.create(req.uri().toString().concat(c.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(c))
                );
    }

    public Mono<ServerResponse> modificar(ServerRequest req) {
        Mono<Cliente> monoCliente = req.bodyToMono(Cliente.class);
        String id = req.pathVariable("id");
        return service.listarPorId(id)
                .zipWith(monoCliente, (bd, cl) -> {
                    bd.setId(id);
                    bd.setNombres(cl.getNombres());
                    bd.setApellidos(cl.getApellidos());
                    bd.setFechaNac(cl.getFechaNac());
                    bd.setUrlFoto(bd.getUrlFoto());
                    return bd;
                })
                .flatMap(validadorGeneral::validate)
                .flatMap(service::modificar)
                .flatMap( c -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(c)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest req) {
        return service.listarPorId(req.pathVariable("id"))
                .flatMap( c -> service.eliminar(c.getId()))
                .flatMap( c -> ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
