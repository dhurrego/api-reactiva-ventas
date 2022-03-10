package com.example.apireactivaventas.controller;

import com.example.apireactivaventas.exceptions.BussinessError;
import com.example.apireactivaventas.security.AuthRequest;
import com.example.apireactivaventas.security.AuthResponse;
import com.example.apireactivaventas.security.JWTUtil;
import com.example.apireactivaventas.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@RestController
public class LoginController {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private IUsuarioService service;

    @PostMapping("login")
    public Mono<ResponseEntity<?>> login(@Valid @RequestBody AuthRequest ar) {
        return service.buscarPorUsuario(ar.getUsername())
                .map((userDetails) -> {
                    if (BCrypt.checkpw(ar.getPassword(), userDetails.getPassword())) {
                        String token = jwtUtil.generateToken(userDetails);
                        Date expiracion = jwtUtil.getExpirationDateFromToken(token);

                        return ResponseEntity.ok(new AuthResponse(token, expiracion));
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BussinessError("Credenciales incorrecta", "401"));
                    }
                }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BussinessError("Credenciales incorrecta", "401")));
    }

}
