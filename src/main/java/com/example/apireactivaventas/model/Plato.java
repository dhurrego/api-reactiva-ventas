package com.example.apireactivaventas.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Document(collection = "platos")
public class Plato {

    @Id
    private String id;

    @NotNull(message = "El nombre es obligatorio")
    @Size(min = 3, message = "El nombre debe tener como minimo 3 caracteres")
    @Field(name = "nombre")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor de cero")
    @Field(name = "precio")
    private Integer precio;

    @NotNull(message = "El estado es obligatorio")
    @Field(name = "estado")
    private Boolean estado;
}
