package com.example.apireactivaventas.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

//Clase S7
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autorizado")))
                .accessDeniedHandler((swe, e) -> Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN)))
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/swagger-resources/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                .pathMatchers("/login").permitAll()
                .pathMatchers("/v2/login").permitAll()
                .pathMatchers("/v2/**").authenticated()
                //.pathMatchers("/v2/**").hasAnyAuthority("ADMIN")
//                .pathMatchers("/v2/**")
//                    .access( (mono, context) ->
//                            mono.map(
//                                auth -> auth.getAuthorities()
//                                        .stream()
//                                        .filter( e -> e.getAuthority().equals("ADMIN"))
//                                        .count() > 0 )
//                            .map(AuthorizationDecision::new)
//                    )
                .pathMatchers("/platos/**").authenticated()
                .pathMatchers("/clientes/**").authenticated()
                .pathMatchers("/facturas/**").authenticated()
                .pathMatchers("/backpressure/**").permitAll()
                .pathMatchers("/usuarios/**").authenticated()
                .pathMatchers("/menus/**").authenticated()
                .anyExchange().authenticated()
                .and()
                .build();


    }
}
