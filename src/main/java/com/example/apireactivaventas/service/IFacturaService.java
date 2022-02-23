package com.example.apireactivaventas.service;

import com.example.apireactivaventas.dto.FiltroDTO;
import com.example.apireactivaventas.model.Factura;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IFacturaService extends ICRUD<Factura, String> {

    Flux<Factura> obtenerFacturasPorFiltro(FiltroDTO filtroDTO);

    Mono<byte[]> generarReporte(String idFactura);

}
