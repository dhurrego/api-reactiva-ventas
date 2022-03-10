package com.example.apireactivaventas.repo;

import com.example.apireactivaventas.model.Rol;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRolRepo extends ReactiveMongoRepository<Rol, String> {
}
