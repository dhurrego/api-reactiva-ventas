package com.example.apireactivaventas.controller;

import com.example.apireactivaventas.dto.FiltroDTO;
import com.example.apireactivaventas.model.Factura;
import com.example.apireactivaventas.pagination.PageSupport;
import com.example.apireactivaventas.service.IFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private IFacturaService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Factura>>> listar() {
        return Mono.just(ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.listar()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Factura>> listarPorId(@PathVariable("id") String id) {
        return service.listarPorId(id)
                .map( f -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(f)
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Factura>> registrar(@Valid @RequestBody  Factura factura, final ServerHttpRequest request) {
        return service.registrar(factura)
                .map( f -> ResponseEntity.created(URI.create(request.getURI().toString().concat("/").concat(f.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(f)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Factura>> modificar(@PathVariable("id") String id,@Valid @RequestBody  Factura factura) {
        return service.listarPorId(id)
                .zipWith(Mono.just(factura), (bd, fa) -> {
                    bd.setId(id);
                    bd.setCliente(fa.getCliente());
                    bd.setDescripcion(fa.getDescripcion());
                    bd.setItems(fa.getItems());
                    bd.setObservacion(fa.getObservacion());
                    return bd;
                })
                .flatMap(service::modificar)
                .map( f -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(f))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id) {
        return service.listarPorId(id)
                .flatMap(f -> service.eliminar(f.getId())
                            .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Factura>>> listarPageable(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageRequest = PageRequest.of(page, size);
        return service.listarPage(pageRequest)
                .map( f -> ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(f))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Factura>> listarHateoasPorId(@PathVariable("id") String id) {
        Mono<Link> link1 = linkTo(methodOn(FacturaController.class).listarPorId(id)).withSelfRel().toMono();

        return service.listarPorId(id)
                .zipWith(link1, (f, lk) -> EntityModel.of(f, lk));
    }

    @PostMapping("/buscar")
    public Mono<ResponseEntity<Flux<Factura>>> buscar(@RequestBody FiltroDTO filtroDTO) {
        return Mono.just(
                ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.obtenerFacturasPorFiltro(filtroDTO))
                );
    }

    @GetMapping("/generarReporte/{id}")
    public Mono<ResponseEntity<byte[]>> generarReporte(@PathVariable("id") String id) {
        return service.generarReporte(id)
                .map(bytes -> ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(bytes)
                ).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
