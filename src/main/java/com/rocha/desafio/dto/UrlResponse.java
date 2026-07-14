package com.rocha.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UrlResponse {
    @Schema(
        description = "URL encurtada gerada pelo sistema para redirecionamento",
        example = "http://localhost:8080/api/v1/url/aB3xD9"
    )
    private String shortUrl;
}
