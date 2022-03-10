package com.example.apireactivaventas.service.impl;

import com.example.apireactivaventas.model.Usuario;
import com.example.apireactivaventas.repo.IRolRepo;
import com.example.apireactivaventas.repo.IUsuarioRepo;
import com.example.apireactivaventas.security.User;
import com.example.apireactivaventas.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioServiceImpl  extends CRUDImpl<Usuario, String> implements IUsuarioService {

    @Autowired
    private IUsuarioRepo repo;

    @Autowired
    private IRolRepo rolRepo;

    @Autowired
    private BCryptPasswordEncoder bcrypt;

    @Override
    protected ReactiveMongoRepository<Usuario, String> getRepo() {
        return repo;
    }

    @Override
    public Mono<User> buscarPorUsuario(String usuario) {
        Mono<Usuario> monoUsuario = repo.findOneByUsuario(usuario);

        List<String> roles = new ArrayList<>();

        return monoUsuario.flatMap(u ->
                Flux.fromIterable(u.getRoles())
                    .flatMap(rol ->
                            rolRepo.findById(rol.getId())
                                .map(r -> {
                                    roles.add(r.getNombre());
                                    return r;
                                })
                    ).collectList().flatMap(list -> {
                        u.setRoles(list);
                        return Mono.just(u);
                    })
                ).flatMap(u -> Mono.just(new User(u.getUsuario(), u.getClave(), u.getEstado(), roles)));
    }

    @Override
    public Mono<Usuario> registrarHash(Usuario usuario) {
        usuario.setClave(bcrypt.encode(usuario.getClave()));
        return repo.save(usuario);
    }
}
