package com.dsp.templateEngine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.dsp.templateEngine.dto.ApplicationInstanceProjection;
import com.dsp.templateEngine.entity.ApplicationInstance;
import com.dsp.templateEngine.repository.ApplicationInstanceRepository;
import com.dsp.templateEngine.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TranslationService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Utils utils;
    @Value("${openKm.dsp1.download.endpoint}")
    private String openKmDsp1DownloadEndPoint;
    @Value("${translation.url.external-translate-api}")
    private String translationExternalApi;
    @Autowired ApplicationInstanceRepository applicationInstanceRepository;

    public String callTranslateFileAPI(String applicationName, String type, String subCategory, LocalDate date,
            boolean edit, File file) {
        String url = translationExternalApi;
        String token = utils.getTokenFromRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);
        // File file = new File("C:/Users/XM628FY/OneDrive -
        // EY/Documents/Desktop/516c058f-8d64-4aba-a271-2e2eae4ef796_v1.docx.docx");
        FileSystemResource fileResource = new FileSystemResource(file);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("type", type);
        body.add("category", applicationName);
        body.add("subCategory", subCategory);
        body.add("date", date);
        body.add("edit", edit);
        body.add("sourceFile", fileResource);
        // 🔹 Combine headers and body
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 🔹 Execute the POST request
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class);
        // 🔹 Return API Response
        return response.getBody();
    }

    public Map<String, Object> sendToTranslation(Long appInstId, ApplicationInstanceProjection appDetails,
            UUID documentUuidEn, String categoryAbbr) {
        Map<String, Object> res = new HashMap<>();
        Optional<ApplicationInstance> instance=applicationInstanceRepository.findById(appInstId);
        File file=getFileFromUUID(documentUuidEn);
        String response=callTranslateFileAPI(categoryAbbr, "ls", categoryAbbr, instance.get().getDocumentDate(), true, file);
        res.put(categoryAbbr, response);
        return res;
    }

    public Map<String,Object> sendToTranslationApi(Long appInstId,
    UUID documentUuidEn,String categoryAbbr)
    {
        Optional<ApplicationInstance> instance=applicationInstanceRepository.findById(appInstId);
        Map<String,Object> res=new HashMap<>();
        File file=getFileFromUUID(documentUuidEn);

        String response=callTranslateFileAPI(categoryAbbr, "ls", categoryAbbr, instance.get().getDocumentDate(), true, file);
        return res;
    }
    public File getFileFromUUID(UUID uuid)
    {
    return getFile(openKmDsp1DownloadEndPoint+"/"+uuid);
    }
    public File getFile(String url)
    {
        try {
            String token = utils.getTokenFromRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    byte[].class
            );
            // Save to temp file
            File tempFile = File.createTempFile("download_", ".docx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(response.getBody());
            }
            System.out.println("✅ File downloaded and saved at: " + tempFile.getAbsolutePath());
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from DSP1: " + e.getMessage(), e);
        }
    }
}
