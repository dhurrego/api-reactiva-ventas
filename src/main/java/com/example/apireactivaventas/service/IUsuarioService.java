package com.example.apireactivaventas.service;

import com.example.apireactivaventas.model.Usuario;
import com.example.apireactivaventas.security.User;
import reactor.core.publisher.Mono;

public interface IUsuarioService extends ICRUD<Usuario, String>{
    Mono<Usuario> registrarHash(Usuario usuario);
    Mono<User> buscarPorUsuario(String usuario);
}
