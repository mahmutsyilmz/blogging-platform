package com.turkcell.blog.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class GoogleGeminiServiceImpl {

    @Value("${google.gemini.api.key}")
    private String geminiApiKey;

    @Value("${google.gemini.api.url}")
    private String geminiApiUrl;

    private final WebClient.Builder webClientBuilder;

    public String summarizeText(String content) {
        // Özetleme prompt'unu oluşturuyoruz.
        String prompt = "Summarize the following text in 60 characters: " + content;

        // İstek gövdesini oluşturuyoruz.
        String requestBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \""
                + prompt.replace("\"", "\\\"")
                + "\" }] }] }";

        // API anahtarını query parametresi olarak ekliyoruz.
        String url = geminiApiUrl + "?key=" + geminiApiKey;

        // WebClient ile API çağrısını yapıyoruz.
        JsonNode responseBody = webClientBuilder.build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofSeconds(10))
                .block();

        if (responseBody != null && responseBody.has("candidates")) {
            JsonNode candidates = responseBody.get("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode candidate = candidates.get(0);
                JsonNode contentNode = candidate.get("content");
                if (contentNode != null) {
                    JsonNode partsNode = contentNode.get("parts");
                    if (partsNode != null && partsNode.isArray() && partsNode.size() > 0) {
                        String summary = partsNode.get(0).get("text").asText();
                        return summary.trim();
                    }
                }
            }
        }
        return "No summary available.";
    }

}
