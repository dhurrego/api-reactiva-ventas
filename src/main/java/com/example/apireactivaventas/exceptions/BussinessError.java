package com.example.apireactivaventas.exceptions;

import lombok.*;

import java.util.Date;

@Getter
@NoArgsConstructor
public class BussinessError {

    @Setter
    private String message;

    @Setter
    private String code;

    private Date timestamp = new Date();

    public BussinessError(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
