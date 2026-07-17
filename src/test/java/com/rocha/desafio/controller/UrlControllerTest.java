package com.rocha.desafio.controller;

import com.rocha.desafio.dto.UrlRequest;
import com.rocha.desafio.dto.UrlResponse;
import com.rocha.desafio.model.UrlMapping; // Ajuste para o pacote real do seu modelo
import com.rocha.desafio.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @Test
    @DisplayName("Deve encurtar a URL com sucesso e retornar status 200 usando UriComponentsBuilder")
    void deveEncurtarUrlComSucesso() {
        // Arrange
        UrlRequest request = new UrlRequest();
        request.setUrl("https://google.com");

        String shortKeyMock = "abc123XYZ";
        when(urlService.shortenUrl("https://google.com")).thenReturn(shortKeyMock);

        // 1. Cria a requisição mockada simulando o domínio "http://localhost:8080"
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);

        // 2. Injeta essa requisição no contexto atual da Thread (o que o Spring espera)
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        try {
            // Act - Agora chama o método sem passar o request por parâmetro
            ResponseEntity<UrlResponse> response = urlController.shorten(request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("http://localhost:8080/api/v1/url/" + shortKeyMock, response.getBody().getShortUrl());

            verify(urlService, times(1)).shortenUrl("https://google.com");

        } finally {
            // 3. Limpa o contexto de requisição após o teste (evita poluir outros testes)
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Test
    @DisplayName("Deve redirecionar para a URL original com sucesso e incrementar cliques (Status 302)")
    void deveRedirecionarParaUrlOriginalComSucesso() {
        // Arrange
        String shortKey = "abc123XYZ";
        String originalUrl = "https://google.com";

        UrlMapping urlMappingMock = new UrlMapping();
        urlMappingMock.setOriginalUrl(originalUrl);

        when(urlService.getOriginalUrl(shortKey)).thenReturn(Optional.of(urlMappingMock));

        // Act
        ResponseEntity<Object> response = urlController.redirect(shortKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode()); // HTTP 302 FOUND
        assertEquals(originalUrl, response.getHeaders().getLocation().toString());

        verify(urlService, times(1)).getOriginalUrl(shortKey);
        verify(urlService, times(1)).incrementClicks(urlMappingMock);
    }

    @Test
    @DisplayName("Deve retornar status 404 quando a shortKey não existir")
    void deveRetornar404QuandoChaveNaoExistir() {
        // Arrange
        String shortKeyInexistente = "chaveFalsa";
        when(urlService.getOriginalUrl(shortKeyInexistente)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = urlController.redirect(shortKeyInexistente);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // HTTP 404 NOT FOUND

        verify(urlService, times(1)).getOriginalUrl(shortKeyInexistente);
        verify(urlService, never()).incrementClicks(any());
    }
}
