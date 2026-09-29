package com.meteora.backend.service;

import com.meteora.backend.dto.request.NewsletterRequest;
import com.meteora.backend.exception.BusinessException;
import com.meteora.backend.model.NewsletterSubscriber;
import com.meteora.backend.repository.NewsletterSubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterSubscriberRepository repository;

    public NewsletterSubscriber inscrever(NewsletterRequest req) {
        if (repository.existsByEmail(req.email())) {
            throw new BusinessException("Este e-mail ja esta cadastrado na newsletter");
        }

        // Gera o cupom de 10% OFF na primeira compra anunciado na home
        String cupom = "BEMVINDO10-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        NewsletterSubscriber assinante = NewsletterSubscriber.builder()
                .email(req.email())
                .cupomBoasVindas(cupom)
                .build();

        return repository.save(assinante);
    }
}
