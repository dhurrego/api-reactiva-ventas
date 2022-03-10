package com.example.apireactivaventas.service;

import com.example.apireactivaventas.model.Menu;
import reactor.core.publisher.Flux;

public interface IMenuService extends ICRUD<Menu, String>{

    Flux<Menu> obtenerMenus(String[] rol);
}