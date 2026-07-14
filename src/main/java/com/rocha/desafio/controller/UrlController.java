package com.rocha.desafio.controller;

import com.rocha.desafio.dto.UrlRequest;
import com.rocha.desafio.dto.UrlResponse;
import com.rocha.desafio.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Encurtador de URL", description = "Endpoints para criação de links curtos e redirecionamento")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/url")
public class UrlController {

    public final UrlService urlService;

    @Operation(
        summary = "Encurta uma URL original",
        description = "Recebe uma URL longa informada pelo usuário e gera uma chave única de acesso reduzido."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "URL encurtada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlResponse.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida ou corpo mal formatado", content = @Content)
    })
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(@Valid @RequestBody UrlRequest urlRequest) { // Adicionado @Valid aqui
        String originalUrl = urlRequest.getUrl();

        String shortKey = urlService.shortenUrl(originalUrl);
        String shortUrl = "http://localhost:8080/api/v1/url/" + shortKey;

        return ResponseEntity.ok(new UrlResponse(shortUrl));
    }

    @Operation(
        summary = "Redireciona para a URL original",
        description = "Recebe o identificador curto (shortKey) e realiza o redirecionamento automático (HTTP 302) para o endereço web original."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirecionamento executado com sucesso", content = @Content),
        @ApiResponse(responseCode = "404", description = "Identificador curto não encontrado no sistema", content = @Content)
    })
    @GetMapping("/{shortKey}")
    public ResponseEntity<Object> redirect(
            @Parameter(description = "Chave identificadora da URL encurtada (Ex: aB3xD9)", required = true)
            @PathVariable String shortKey) {

        return urlService.getOriginalUrl(shortKey)
                .map(urlMapping -> {
                    // Incrementa o clique antes de redirecionar
                    urlService.incrementClicks(urlMapping);

                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create(urlMapping.getOriginalUrl()))
                            .build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
