package com.example.apireactivaventas.service.impl;

import com.example.apireactivaventas.model.Plato;
import com.example.apireactivaventas.repo.IPlatoRepo;
import com.example.apireactivaventas.service.IPlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PlatoServiceImpl extends CRUDImpl<Plato, String> implements IPlatoService {

    @Autowired
    private IPlatoRepo repo;

    @Override
    protected ReactiveMongoRepository getRepo() {
        return repo;
    }
}
