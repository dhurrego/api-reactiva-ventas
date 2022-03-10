package com.example.apireactivaventas.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

//Clase S2
@Data
@AllArgsConstructor
public class AuthRequest {
    @NotEmpty(message = "El nombre de usuario es obligatorio")
    private String username;
    @NotEmpty(message = "La contraseña es obligatoria")
    private String password;
}
