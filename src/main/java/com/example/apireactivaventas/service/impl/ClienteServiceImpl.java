package com.example.apireactivaventas.service.impl;

import com.example.apireactivaventas.model.Cliente;
import com.example.apireactivaventas.repo.IClienteRepo;
import com.example.apireactivaventas.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl extends CRUDImpl<Cliente, String> implements IClienteService {

    @Autowired
    private IClienteRepo repo;

    @Override
    protected ReactiveMongoRepository getRepo() {
        return repo;
    }
}
