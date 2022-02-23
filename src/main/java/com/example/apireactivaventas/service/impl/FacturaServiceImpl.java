package com.example.apireactivaventas.service.impl;

import com.example.apireactivaventas.dto.FiltroDTO;
import com.example.apireactivaventas.model.Factura;
import com.example.apireactivaventas.repo.IClienteRepo;
import com.example.apireactivaventas.repo.IFacturaRepo;
import com.example.apireactivaventas.repo.IPlatoRepo;
import com.example.apireactivaventas.service.IFacturaService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class FacturaServiceImpl extends CRUDImpl<Factura, String> implements IFacturaService {

    @Autowired
    private IFacturaRepo repo;

    @Autowired
    private IClienteRepo clienteRepo;

    @Autowired
    private IPlatoRepo platoRepo;

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
        return repo.findById(idFactura)
                .flatMap( factura -> Mono.just(factura)
                                        .zipWith(clienteRepo.findById(factura.getCliente().getId()), (fa, cl) -> {
                                            fa.setCliente(cl);
                                            return fa;
                                        })
                ).flatMap(factura -> Flux.fromIterable(factura.getItems())
                                        .flatMap( item -> platoRepo.findById(item.getPlato().getId())
                                                            .map(plato -> {
                                                                item.setPlato(plato);
                                                                return item;
                                                            })
                                        ).collectList()
                                        .flatMap( list -> {
                                            factura.setItems(list);
                                            return Mono.just(factura);
                                        })
                ).map(factura -> {
                    InputStream stream;
                    try {
                        Map<String, Object> parametros = new HashMap<>();
                        parametros.put("txt_cliente", factura.getCliente().getNombres().concat(" ").concat(factura.getCliente().getApellidos()));
                        stream = getClass().getResourceAsStream("/facturas.jrxml");
                        JasperReport report = JasperCompileManager.compileReport(stream);
                        JasperPrint print = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(factura.getItems()));
                        return JasperExportManager.exportReportToPdf(print);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new byte[0];
                });
    }
}
