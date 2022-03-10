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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

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
        BussinessError errorResponse = new BussinessError();

        HttpStatus httpStatus;
        String statusCode = String.valueOf(errorGeneral.get("status"));

        switch (statusCode) {
            case "400":
                try {
                    errorResponse.setCode("400");
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
                    errorResponse.setMessage(mensaje);
                    httpStatus = HttpStatus.BAD_REQUEST;
                } catch (Exception e) {
                    errorResponse.setCode("500");
                    errorResponse.setMessage("Error general del backend");
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }

                break;
            case "406":
                errorResponse.setCode("406");
                errorResponse.setMessage("Archivo no subido correctamente");
                httpStatus = HttpStatus.NOT_ACCEPTABLE;
                break;
            case "401":
                errorResponse.setCode("401");
                String mensajeNoAutorizado;
                Throwable errorNoAutorizado = getError(request);
                if (errorNoAutorizado instanceof ResponseStatusException) {
                    mensajeNoAutorizado = ((ResponseStatusException) errorNoAutorizado).getReason();
                } else {
                    mensajeNoAutorizado = "No autorizado";
                }
                errorResponse.setMessage(mensajeNoAutorizado);
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            case "403":
                errorResponse.setCode("403");
                errorResponse.setMessage("Acceso denegado");
                httpStatus = HttpStatus.FORBIDDEN;
                break;
            default:
                errorResponse.setCode("500");
                errorResponse.setMessage("Error general del backend");
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}
