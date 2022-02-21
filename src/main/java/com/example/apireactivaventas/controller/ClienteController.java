package com.example.apireactivaventas.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.apireactivaventas.model.Cliente;
import com.example.apireactivaventas.pagination.PageSupport;
import com.example.apireactivaventas.service.IClienteService;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private IClienteService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Cliente>>> listar() {
        return Mono.just(ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.listar()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Cliente>> listarPorId(@PathVariable("id") String id) {
        return service.listarPorId(id)
                .map( c -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c)
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Cliente>> registrar(@Valid @RequestBody  Cliente cliente, final ServerHttpRequest request) {
        return service.registrar(cliente)
                .map( c -> ResponseEntity.created(URI.create(request.getURI().toString().concat("/").concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Cliente>> modificar(@PathVariable("id") String id,@Valid @RequestBody  Cliente cliente) {
        return service.listarPorId(id)
                .zipWith(Mono.just(cliente), (bd, cl) -> {
                    bd.setId(id);
                    bd.setNombres(cl.getNombres());
                    bd.setApellidos(cl.getApellidos());
                    bd.setFechaNac(cl.getFechaNac());
                    bd.setUrlFoto(bd.getUrlFoto());
                    return bd;
                })
                .flatMap(service::modificar)
                .map( c -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id) {
        return service.listarPorId(id)
                .flatMap(c -> service.eliminar(c.getId())
                            .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Cliente>>> listarPageable(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageRequest = PageRequest.of(page, size);
        return service.listarPage(pageRequest)
                .map( c -> ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(c))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Cliente>> listarHateoasPorId(@PathVariable("id") String id) {
        Mono<Link> link1 = linkTo(methodOn(ClienteController.class).listarPorId(id)).withSelfRel().toMono();

        return service.listarPorId(id)
                .zipWith(link1, (c, lk) -> EntityModel.of(c, lk));
    }

    @PostMapping("/v1/subir/{id}")
    public Mono<ResponseEntity<Cliente>> subirV1(@PathVariable String id, @RequestPart FilePart file) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "d3iuksq3",
                "api_key", "XXXXXXXXX",
                "api_secret", "XXXXXXXXX"));
        File f = Files.createTempFile("temp", file.filename()).toFile();

        return file.transferTo(f)
                .then(service.listarPorId(id)
                        .flatMap( c -> {
                            Map response;
                            try {
                                response = cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type", "auto"));

                                JSONObject json = new JSONObject(response);
                                String url = json.getString("url");
                                c.setUrlFoto(url);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return service.modificar(c).then(Mono.just(ResponseEntity.ok().body(c)));
                        })
                        .defaultIfEmpty(ResponseEntity.notFound().build())
                );
    }

    @PostMapping("/v2/subir/{id}")
    public Mono<ResponseEntity<Cliente>> subirV2(@PathVariable String id, @RequestPart FilePart file) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "d3iuksq3",
                "api_key", "XXXXXXXXX",
                "api_secret", "XXXXXXXXX"));

        return service.listarPorId(id)
                .flatMap( c -> {
                    try {
                        File f = Files.createTempFile("temp", file.filename()).toFile();
                        file.transferTo(f).block();

                        Map response = cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type", "auto"));
                        JSONObject json = new JSONObject(response);
                        String url = json.getString("url");
                        c.setUrlFoto(url);
                        return service.modificar(c).thenReturn(ResponseEntity.ok().body(c));
                    } catch (Exception e) {

                    }
                    return Mono.just(ResponseEntity.ok().body(c));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
