package com.example.apireactivaventas.repo;

import com.example.apireactivaventas.model.Factura;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface IFacturaRepo extends ReactiveMongoRepository<Factura, String> {

    @Query("{'cliente' : { _id: ?0 } }")
    Flux<Factura> obtenerFacturasPorCliente(String idCliente);

    @Query("{'creado_en': { $gte: ?0, $lt: ?1 }}")
    Flux<Factura> obtenerFacturasPorFecha(LocalDate fechaInicio, LocalDate fechaFin);
}
