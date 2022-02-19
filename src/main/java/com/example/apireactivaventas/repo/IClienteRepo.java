package com.example.apireactivaventas.repo;

import com.example.apireactivaventas.model.Cliente;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClienteRepo extends ReactiveMongoRepository<Cliente, String> {
}
