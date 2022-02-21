package com.example.apireactivaventas.repo;

import com.example.apireactivaventas.model.Factura;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacturaRepo extends ReactiveMongoRepository<Factura, String> {
}
