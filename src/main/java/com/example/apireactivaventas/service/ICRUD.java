package com.example.apireactivaventas.service;

import com.example.apireactivaventas.pagination.PageSupport;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICRUD<T, ID> {

    Mono<T> registrar(T t);
    Mono<T> modificar(T t);
    Flux<T> listar();
    Mono<T> listarPorId(ID id);
    Mono<Void> eliminar(ID id);
    Mono<PageSupport<T>> listarPage(Pageable pageable);
}
