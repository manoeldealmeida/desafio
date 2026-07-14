package com.rocha.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de requisição para encurtar URL")
public class UrlRequest {

    @Schema(description = "URL completa que será encurtada pelo sistema", example = "https://google.com")
    @NotBlank(message = "A URL não pode estar vazia")
    @URL(message = "Por favor, insira um formato de URL válido (ex: https://exemplo.com)")
    private String url;
}
