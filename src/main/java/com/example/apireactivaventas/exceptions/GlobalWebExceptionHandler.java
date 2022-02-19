package com.example.apireactivaventas.exceptions;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(-1)
public class GlobalWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalWebExceptionHandler(ErrorAttributes errorAttributes, Resources resources,
                                     ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> errorGeneral = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        Map<String, Object> mapException = new HashMap<>();
        final String MESSAGE_KEY = "message";

        HttpStatus httpStatus;
        String statusCode = String.valueOf(errorGeneral.get("status"));

        switch (statusCode) {
            case "500":
                mapException.put("code", "500");
                mapException.put(MESSAGE_KEY, "Error general del backend");
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            case "400":
                try {
                    mapException.put("code", "400");
                    String mensaje;
                    Throwable error = getError(request);
                    if (error instanceof WebExchangeBindException) {
                        mensaje = ((WebExchangeBindException) error).getBindingResult()
                                    .getAllErrors()
                                    .get(0)
                                    .getDefaultMessage();
                    } else {
                        mensaje = "Petición incorrecta";
                    }
                    mapException.put(MESSAGE_KEY, mensaje);
                    httpStatus = HttpStatus.BAD_REQUEST;
                } catch (Exception e) {
                    mapException.put("code", "500");
                    mapException.put(MESSAGE_KEY, "Error general del backend");
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }

                break;
            case "406":
                mapException.put("code", "406");
                mapException.put(MESSAGE_KEY, "Archivo no subido correctamente");
                httpStatus = HttpStatus.NOT_ACCEPTABLE;
                break;
            default:
                mapException.put("code", "900");
                mapException.put(MESSAGE_KEY, errorGeneral.get("error"));
                httpStatus = HttpStatus.CONFLICT;
                break;
        }

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapException));
    }
}
