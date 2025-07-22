package com.dsp.templateEngine.translater.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class GetTranslatedFileController {

    private final WebClient webClient;
    private final String translationApiUrl;

    public GetTranslatedFileController(WebClient.Builder builder,
                                       @Value("${file.api.translation-url}") String translationApiUrl) {
        this.webClient = builder.build();
        this.translationApiUrl = translationApiUrl;
    }

    @GetMapping("/get-translated-file")
    public ResponseEntity<?> getTranslatedFile(
            @RequestParam("sourceUrl") String sourceUrl,
            @RequestParam("locale") String locale,
            @RequestParam("type") String type
    ) {
        try {
            String encodedUrl = URLEncoder.encode(sourceUrl, StandardCharsets.UTF_8);
            String fullUrl = translationApiUrl + "?sourceUrl=" + encodedUrl + "&locale=" + locale + "&type=" + type;

            // Use WebClient to send GET request and retrieve full response
            ResponseEntity<String> responseEntity = webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("Remote API Error: " + errorBody)))
                    )
                    .toEntity(String.class)
                    .block();

            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
            String body = responseEntity.getBody();

            // Handle known status codes
            switch (statusCode.value()) {
                case 200:
                    return ResponseEntity.ok().body(body);

                case 202:
                    return ResponseEntity.status(HttpStatus.ACCEPTED)
                            .body("Translation job accepted. File not ready yet. Response: " + body);

                case 400:
                    return ResponseEntity.badRequest().body("Bad Request: Missing or invalid parameters. Response: " + body);

                case 404:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Not Found: Translation not found for given sourceUrl. Response: " + body);

                case 500:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Internal Server Error: Response: " + body);

                default:
                    return ResponseEntity.status(statusCode)
                            .body("Unhandled status code: " + statusCode + ". Response: " + body);
            }

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Wrapper Error: " + ex.getMessage());
        }
    }
}
