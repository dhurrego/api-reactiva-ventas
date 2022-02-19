package com.example.apireactivaventas.repo;

import com.example.apireactivaventas.model.Plato;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPlatoRepo extends ReactiveMongoRepository<Plato, String> {
}
