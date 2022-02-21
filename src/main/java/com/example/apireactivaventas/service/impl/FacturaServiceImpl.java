package com.example.apireactivaventas.service.impl;

import com.example.apireactivaventas.model.Factura;
import com.example.apireactivaventas.repo.IFacturaRepo;
import com.example.apireactivaventas.service.IFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class FacturaServiceImpl extends CRUDImpl<Factura, String> implements IFacturaService {

    @Autowired
    private IFacturaRepo repo;

    @Override
    protected ReactiveMongoRepository getRepo() {
        return repo;
    }
}
