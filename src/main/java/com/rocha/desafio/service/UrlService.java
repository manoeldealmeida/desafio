package com.rocha.desafio.service;

import com.rocha.desafio.model.UrlMapping;
import com.rocha.desafio.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public String shortenUrl(String originalUrl) {
        String shortKey = RandomStringUtils.insecure().nextAlphanumeric(6);
        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .shortKey(shortKey)
                .build();
        urlRepository.save(urlMapping);
        return shortKey;
    }

    @Cacheable(value = "urls", key = "#shortKey", unless = "#result == null or !#result.isPresent()")
    public Optional<UrlMapping> getOriginalUrl(String shortKey) {
        return urlRepository.findByShortKeyIgnoreCase(shortKey);
    }

    @Transactional
    public void incrementClicks(UrlMapping urlMapping) {
        urlMapping.incrementClick();
        urlRepository.save(urlMapping);
    }
}