package com.meteora.backend.controller;

import com.meteora.backend.dto.request.NewsletterRequest;
import com.meteora.backend.model.NewsletterSubscriber;
import com.meteora.backend.service.NewsletterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Cobre o formulario de cadastro de e-mail do rodape da home, hoje sem action/method */
@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> inscrever(@Valid @RequestBody NewsletterRequest req) {
        NewsletterSubscriber assinante = newsletterService.inscrever(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensagem", "Inscricao realizada com sucesso",
                        "cupom", assinante.getCupomBoasVindas()
                ));
    }
}
