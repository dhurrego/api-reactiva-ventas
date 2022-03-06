package com.example.apireactivaventas.controller;

import com.example.apireactivaventas.model.Plato;
import com.example.apireactivaventas.pagination.PageSupport;
import com.example.apireactivaventas.service.IPlatoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/platos")
public class PlatoController {

    private static final Logger log = LoggerFactory.getLogger((PlatoController.class));

    @Autowired
    private IPlatoService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Plato>>> listar() {

        return Mono.just(ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.listar()
                            .parallel()
                            .runOn(Schedulers.parallel())
                            .groups()
                            .flatMap( gf -> gf)
                    )
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Plato>> listarPorId(@PathVariable("id") String id) {
        return service.listarPorId(id)
                .map( p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Plato>> registrar(@Valid @RequestBody  Plato plato, final ServerHttpRequest request) {
        return service.registrar(plato)
                .map( p -> ResponseEntity.created(URI.create(request.getURI().toString().concat("/").concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Plato>> modificar(@PathVariable("id") String id,@Valid @RequestBody  Plato plato) {
        return service.listarPorId(id)
                .zipWith(Mono.just(plato), (bd, pl) -> {
                    bd.setId(id);
                    bd.setNombre(pl.getNombre());
                    bd.setPrecio(pl.getPrecio());
                    bd.setEstado(pl.getEstado());
                    return bd;
                })
                .flatMap(service::modificar)
                .map( p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id) {
        return service.listarPorId(id)
                .flatMap(p -> service.eliminar(p.getId())
                            .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Plato>>> listarPageable(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageRequest = PageRequest.of(page, size);
        return service.listarPage(pageRequest)
                .map( p -> ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Plato>> listarHateoasPorId(@PathVariable("id") String id) {
        Mono<Link> link1 = linkTo(methodOn(PlatoController.class).listarPorId(id)).withSelfRel().toMono();

        return service.listarPorId(id)
                .zipWith(link1, (p, lk) -> EntityModel.of(p, lk));
    }

}
