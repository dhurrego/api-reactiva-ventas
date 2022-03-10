package com.example.apireactivaventas.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//Clase S5
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private JWTUtil jwtUtil;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        String usuario;

        try {
            usuario = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            usuario = null;
        }

        if(Objects.nonNull(usuario) && jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.getAllClaimsFromToken(token);

            List<String> rolesMap = claims.get("roles", List.class);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    usuario,
                    null,
                    rolesMap.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            return Mono.just(auth);
        } else {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido o ha expirado"));
        }
    }
}
