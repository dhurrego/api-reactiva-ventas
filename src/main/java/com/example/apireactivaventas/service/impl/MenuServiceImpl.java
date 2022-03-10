package com.example.apireactivaventas.service.impl;

import com.example.apireactivaventas.model.Menu;
import com.example.apireactivaventas.repo.IMenuRepo;
import com.example.apireactivaventas.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MenuServiceImpl extends CRUDImpl<Menu, String> implements IMenuService {

    @Autowired
    private IMenuRepo repo;

    @Override
    protected ReactiveMongoRepository<Menu, String> getRepo() {
        return repo;
    }

    @Override
    public Flux<Menu> obtenerMenus(String[] roles) {
        return repo.obtenerMenus(roles);
    }

}
