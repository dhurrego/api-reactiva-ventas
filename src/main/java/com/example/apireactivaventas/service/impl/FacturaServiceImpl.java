package com.example.apireactivaventas.service.impl;

import com.example.apireactivaventas.dto.FiltroDTO;
import com.example.apireactivaventas.model.Factura;
import com.example.apireactivaventas.repo.IFacturaRepo;
import com.example.apireactivaventas.service.IFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class FacturaServiceImpl extends CRUDImpl<Factura, String> implements IFacturaService {

    @Autowired
    private IFacturaRepo repo;

    @Override
    protected ReactiveMongoRepository getRepo() {
        return repo;
    }

    @Override
    public Flux<Factura> obtenerFacturasPorFiltro(FiltroDTO filtroDTO) {
        String criterio = Objects.nonNull(filtroDTO.getIdCliente()) ? "C" : "O";

        if(criterio.equalsIgnoreCase("C")) {
            return repo.obtenerFacturasPorCliente(filtroDTO.getIdCliente());
        } else {
            return repo.obtenerFacturasPorFecha(filtroDTO.getFechaInicio(), filtroDTO.getFechaFin().plusDays(1));
        }
    }

    @Override
    public Mono<byte[]> generarReporte(String idFactura) {
        return null;
    }
}
