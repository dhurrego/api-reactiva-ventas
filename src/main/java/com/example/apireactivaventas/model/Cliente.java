package com.example.apireactivaventas.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Document(collection = "clientes")
public class Cliente {

    @Id
    private String id;

    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÑáéíóúÁÉÍÓÚ ]*$", message = "El nombre tiene caracteres no permitidos")
    @NotNull(message = "EL nombre es obligatorio")
    @Field(name = "nombres")
    private String nombres;

    @Size(min = 3, max = 150, message = "El apellido debe tener entre 3 y 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÑáéíóúÁÉÍÓÚ ]*$", message = "El apellido tiene caracteres no permitidos")
    @NotNull(message = "EL apellido es obligatorio")
    @Field(name = "apellidos")
    private String apellidos;

    @Past(message = "La fecha nacimiento debe ser menor a la fecha actual")
    @NotNull(message = "La fecha nacimiento es obligatoria")
    @Field(name = "fechaNac")
    private LocalDate fechaNac;

    @Field(name = "urlFoto")
    private String urlFoto;

}
