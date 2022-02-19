package com.example.apireactivaventas.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    @Field(name = "usuario")
    private String usuario;

    @Field(name = "clave")
    private String clave;

    @Field(name = "estado")
    private Boolean estado;

    @Field(name = "roles")
    private List<Rol> roles;
}
