package com.dsp.templateEngine.translater.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/translater-api")
public class TranslateController {

    private final WebClient webClient;

    public TranslateController(WebClient.Builder builder,
                               @Value("${translation.api.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @PostMapping("/translate-file")
    public ResponseEntity<?> translateFile(
            @RequestParam("sourceFile") MultipartFile sourceFile,
            @RequestParam("type") String type,
            @RequestParam("category") String category,
            @RequestParam("subCategory") String subCategory,
            @RequestParam("date") String date,
            @RequestParam(value = "edit", required = false) String editFlag
    ) throws IOException {

        // Save uploaded file to a temporary location
        File tempFile = File.createTempFile("upload-", sourceFile.getOriginalFilename());
        sourceFile.transferTo(tempFile);
        FileSystemResource fileResource = new FileSystemResource(tempFile);

        // Form body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("sourceFile", fileResource);
        body.add("type", type);
        body.add("category", category);
        body.add("subCategory", subCategory);
        body.add("date", date);
        if (editFlag != null) {
            body.add("edit", editFlag);
        }

        // Make API call
        String response = webClient.post()
                .uri("/api/v1/translate-file")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class).map(RuntimeException::new))
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
