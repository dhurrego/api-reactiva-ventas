package com.example.apireactivaventas.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FiltroDTO {
    private String idCliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
