package com.example.apireactivaventas.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Component
public class RequestValidators {

    @Autowired
    private Validator validador;

    public <T> Mono<T> validate(T obj) {
        if(Objects.isNull(obj)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }

        Set<ConstraintViolation<T>> violations = this.validador.validate(obj);
        if(Objects.isNull(violations) || violations.isEmpty()) {
            return Mono.just(obj);
        }

        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
}
