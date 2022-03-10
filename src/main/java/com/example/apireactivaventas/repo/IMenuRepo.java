package com.example.apireactivaventas.repo;

import com.example.apireactivaventas.model.Menu;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface IMenuRepo extends ReactiveMongoRepository<Menu, String> {

    @Query("{'roles' : { $in: ?0 }}")
    Flux<Menu> obtenerMenus(String[] roles);
}

