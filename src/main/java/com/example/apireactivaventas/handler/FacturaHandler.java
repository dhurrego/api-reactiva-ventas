package com.example.apireactivaventas.handler;

import com.example.apireactivaventas.model.Factura;
import com.example.apireactivaventas.service.IFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class FacturaHandler {

    @Autowired
    private IFacturaService service;

    public Mono<ServerResponse> listar(ServerRequest req) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.listar(), Factura.class);
    }

    public Mono<ServerResponse> listarPorId(ServerRequest req) {
        return service.listarPorId(req.pathVariable("id"))
                .flatMap( f -> ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(f)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> registrar(ServerRequest req) {
        Mono<Factura> monoFactura = req.bodyToMono(Factura.class);
        return monoFactura
                .flatMap( f -> service.registrar(f))
                .flatMap( f -> ServerResponse
                                .created(URI.create(req.uri().toString().concat(f.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(f))
                );
    }

    public Mono<ServerResponse> modificar(ServerRequest req) {
        Mono<Factura> monoFactura = req.bodyToMono(Factura.class);
        String id = req.pathVariable("id");
        return service.listarPorId(id)
                .zipWith(monoFactura, (bd, fa) -> {
                    bd.setId(id);
                    bd.setCliente(fa.getCliente());
                    bd.setDescripcion(fa.getDescripcion());
                    bd.setItems(fa.getItems());
                    bd.setObservacion(fa.getObservacion());
                    return bd;
                })
                .flatMap(service::modificar)
                .flatMap( f -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(f)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest req) {
        return service.listarPorId(req.pathVariable("id"))
                .flatMap( f -> service.eliminar(f.getId()))
                .flatMap( f -> ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
