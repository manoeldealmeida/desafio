package com.rocha.desafio.service;

import com.rocha.desafio.model.UrlMapping;
import com.rocha.desafio.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    @DisplayName("Deve encurtar a URL com sucesso e salvar no repositório")
    void deveEncurtarUrlComSucesso() {
        // Arrange
        String urlOriginal = "https://google.com";
        // Capturamos o argumento enviado para o save para validar as propriedades internas
        ArgumentCaptor<UrlMapping> urlMappingCaptor = ArgumentCaptor.forClass(UrlMapping.class);
        when(urlRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String shortKey = urlService.shortenUrl(urlOriginal);

        // Assert
        assertNotNull(shortKey);
        assertEquals(6, shortKey.length(), "A chave gerada deve possuir exatamente 6 caracteres");

        verify(urlRepository, times(1)).save(urlMappingCaptor.capture());
        UrlMapping savedMapping = urlMappingCaptor.getValue();
        assertEquals(urlOriginal, savedMapping.getOriginalUrl());
        assertEquals(shortKey, savedMapping.getShortKey());
    }

    @Test
    @DisplayName("Deve retornar UrlMapping quando a shortKey existir no banco")
    void deveRetornarUrlQuandoShortKeyExistir() {
        // Arrange
        String shortKey = "aB3xD9";
        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl("https://google.com")
                .shortKey(shortKey)
                .build();

        when(urlRepository.findByShortKeyIgnoreCase(shortKey)).thenReturn(Optional.of(urlMapping));

        // Act
        Optional<UrlMapping> resultado = urlService.getOriginalUrl(shortKey);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("https://google.com", resultado.get().getOriginalUrl());
        assertEquals(shortKey, resultado.get().getShortKey());
        verify(urlRepository, times(1)).findByShortKeyIgnoreCase(shortKey);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando a shortKey não existir")
    void deveRetornarVazioQuandoShortKeyNaoExistir() {
        // Arrange
        String shortKey = "inexistente";
        when(urlRepository.findByShortKeyIgnoreCase(shortKey)).thenReturn(Optional.empty());

        // Act
        Optional<UrlMapping> resultado = urlService.getOriginalUrl(shortKey);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(urlRepository, times(1)).findByShortKeyIgnoreCase(shortKey);
    }

    @Test
    @DisplayName("Deve incrementar cliques e salvar o registro atualizado")
    void deveIncrementarCliquesComSucesso() {
        // Arrange
        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl("https://google.com")
                .shortKey("aB3xD9")
                .clickCount(10L)
                .build();

        // Act
        urlService.incrementClicks(urlMapping);

        // Assert
        assertEquals(11L, urlMapping.getClickCount(), "O contador de cliques deve ser incrementado em 1");
        verify(urlRepository, times(1)).save(urlMapping);
    }
}
