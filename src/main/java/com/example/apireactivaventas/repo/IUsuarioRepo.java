package com.example.apireactivaventas.repo;

import com.example.apireactivaventas.model.Usuario;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IUsuarioRepo extends ReactiveMongoRepository<Usuario, String> {
    Mono<Usuario> findOneByUsuario(String usuario);
}
