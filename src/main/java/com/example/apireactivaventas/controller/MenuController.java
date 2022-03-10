package com.example.apireactivaventas.controller;

import com.example.apireactivaventas.model.Menu;
import com.example.apireactivaventas.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/menus")
public class MenuController {

    @Autowired
    private IMenuService service;

    //CONSULTAR DE ACUERDO AL ROL DEL USUARIO QUE INICIO, DEVOLVER SUS MENUS
    @GetMapping
    public Mono<ResponseEntity<Flux<Menu>>> listar(){
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getAuthorities)
                .map(roles -> {
                    String rolesString = roles.stream().map(Object::toString).collect(Collectors.joining(",")); // "ADMIN,USER"
                    String[] strings = rolesString.split(",");
                    return service.obtenerMenus(strings);
                })
                .flatMap(fx -> {
                    return Mono.just(ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fx));
                });
    }
}
