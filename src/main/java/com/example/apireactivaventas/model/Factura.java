package com.example.apireactivaventas.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "facturas")
public class Factura {

    @Id
    private String id;

    @NotEmpty(message = "La descripción es obligatoria")
    @Field(name = "descripcion")
    private String descripcion;

    @NotEmpty(message = "La observación es obligatoria")
    @Field(name = "observacion")
    private String observacion;

    @NotNull(message = "El cliente es obligatorio")
    @Field(name = "cliente")
    private Cliente cliente;

    @NotEmpty(message = "La lista de items es obligatoria y no puede estar vacia")
    @Field(name = "items")
    private List<FacturaItem> items;

    @Field(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();
}
