package com.example.apireactivaventas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@RestController
@RequestMapping("/backpressure")
public class BackPressureController {

    @GetMapping(value = "/buffer", produces = "application/stream+json")
    public Flux<Integer> testContrapresion() {
        return Flux.range(1, 2000)
                .parallel()
                .runOn(Schedulers.parallel())
                .groups()
                .flatMap( gf -> gf)
                .log()
                .limitRate(32);

    }
}
