package com.rocha.desafio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "urls")
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortKey;

    @Builder.Default // Garante que o Lombok Builder use o valor padrão abaixo
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

    // Adicione este campo dentro da sua classe UrlMapping.java
    @Builder.Default
    @Column(nullable = false)
    private Long clickCount = 0L;

    // Adicione este método utilitário para incrementar os cliques
    public void incrementClick() {
        this.clickCount++;
    }
}
