package com.dsp.templateEngine.translation;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RegionalTranslationService {
	
    public static final Map<String, String> CATEGORY_MAP = new HashMap<>();

    public static final Map<String, String> SUB_CATEGORY_MAP = new HashMap<>();
    
    public static final Map<String, String> HOUSE_MAP = new HashMap<>();
    
    static {
        CATEGORY_MAP.put("LOB", "List of Business");
        CATEGORY_MAP.put("B1", "Bulletin Part-I");
        CATEGORY_MAP.put("B2", "Bulletin Part-II");
//        CATEGORY_MAP.put("SYN", "Synopsis");
//        CATEGORY_MAP.put("PLOT", "Paper to be Laid");
    }

    static {
        SUB_CATEGORY_MAP.put("List Of Business", "Normal");
        SUB_CATEGORY_MAP.put("Revised List Of Business", "Revised List of Business");
        SUB_CATEGORY_MAP.put("Supplementary List Of Business - 1", "Supplementary List of Business-1");
        SUB_CATEGORY_MAP.put("Supplementary List Of Business - 2", "Supplementary List of Business-2");
        SUB_CATEGORY_MAP.put("Supplementary List Of Business - 3", "Supplementary List of Business-3");
        
        // Synopsis to be added later
    }
    
    static {
        HOUSE_MAP.put("RS", "rs");
        HOUSE_MAP.put("LS", "ls");
    }
    
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
	@Value("${regional.language.translation.url}")
	private String baseUrl;
	
	public ResponseEntity<String> sendForTranslation (RegionalTranslationDto input) {
		
        RestTemplate restTemplate = new RestTemplate();

        // Base URL of your service
        String url = buildTranslateUrl(baseUrl, input.isEditFlag()); // edit=t

        if (!validate(input)) {
        	return null;
        }
        
        populateEndPointParams(input);
        
         // Prepare multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();        
        body.add("sourceFile", new FileSystemResource(input.getSource()));        
        body.add("type", input.getType());        
        body.add("category", input.getCategory());        
        body.add("subCategory", input.getSubCategory());        
        body.add("date", input.getSittingDate().format(formatter));

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        System.out.println("Request: " + requestEntity);

        // Invoke API
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Output result
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());
        
        return response;
    }

	
	private static String buildTranslateUrl(String baseUrl, Boolean edit) {
        String url = baseUrl + "/api/v1/translate-file";
        if (Boolean.TRUE.equals(edit)) {
            url += "?edit=true";
        }
        return url;
    }
	
	private boolean validate(RegionalTranslationDto input) {
		
		// Validate Mandatory Parameters
		if (StringUtils.isBlank(input.getDocumentType()) ||
			StringUtils.isBlank(input.getFileUrl()) ||
			input.getSittingDate() == null) {
			return false;
		}
		
		// validate application. Only specific documents are to be sent for 
		if (!CATEGORY_MAP.containsKey(input.getDocumentType())) {
			return false;
		}
	
		// validate file exists 
        URI uri = URI.create(input.getFileUrl());
        if (uri == null) {
        	return false;
        }

        return true;
	}
    
	
	private void populateEndPointParams (RegionalTranslationDto input) {
	    
		// Source file
         try {
	        URL url = new URL(input.getFileUrl());
	        Path temp = Files.createTempFile(UUID.randomUUID().toString(), ".docx");
	        Files.copy(url.openStream(), temp, StandardCopyOption.REPLACE_EXISTING);
	        input.setSource(temp.toFile());
        } catch (Exception e) {
        	log.error("Failed to download file for sending to translation: {} ", e.getMessage());
        }
  
        // Category
        input.setCategory(CATEGORY_MAP.get(input.getDocumentType()));
        
        // Sub Category
        String subCategory = !StringUtils.isBlank(input.getDocumentSubType()) ?
        						(SUB_CATEGORY_MAP.get(input.getDocumentSubType()) != null ? 
        						 SUB_CATEGORY_MAP.get(input.getDocumentSubType()) : null) 
        					  : StringUtils.EMPTY;
        
        input.setSubCategory(subCategory);
        
        // House
        input.setType(HOUSE_MAP.get("LS"));
        
        return;
	}
	
	// Fetch Translated File
    public String getTranslatedFile(String sourceUrl, String locale, String type) { 

        RestTemplate restTemplate = new RestTemplate();

        // Base URL of your service
//        String url = baseUrl + "/api/v1/translated-files";
        String url = baseUrl + "/api/avk_file/translation";

         // Prepare multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();        
        body.add("sourceUrl", sourceUrl);        
        body.add("type", type);        
        body.add("locale", locale);        

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        System.out.println("Request: " + requestEntity);

        // Invoke API
//      ResponseEntity<byte[]> response = restTemplate.exchange(
        ResponseEntity<String> response = restTemplate.exchange(		
                url,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // Output result
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());
        
//        File file = null;
//        try {
//	        if (response.getStatusCode() == HttpStatus.OK) {
//	
//	            // Create output file
//	            file = new File("translated_output.docx");
//	
//	            try (FileOutputStream fos = new FileOutputStream(file)) {
//	                fos.write(response.getBody());
//	            }
//	
//	            System.out.println("File saved: " + file.getAbsolutePath());
//	        }
//        } catch (Exception e) {
//        	log.error("Failed to retrieve file: {}", e.getMessage());
//        }
//        return file;
        
        return response.getBody();
    }	
}
