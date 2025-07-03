package com.dsp.templateEngine.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class LibreConverter implements IConverter {

	//@Autowired
	//RestTemplate restTemplate;
	
	@Value("${libre.service.url}")
	private String libreServiceUrl;
	
	private static final String LIBRE_END_POINT = "/convert"; 
	
	public File convertHtmlToPdf(String htmlContent, String fileName, Integer startPageNumber) throws IOException {

		File tempPdfFile = File.createTempFile(fileName, ".pdf");

		String endpointUrl = libreServiceUrl + LIBRE_END_POINT;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		headers.set("Accept-Language", "hi-IN");
		
		// Prepare the request entity with headers and body
		HttpEntity<String> requestEntity = new HttpEntity<>(htmlContent, headers);

		// Use Rest Template to call Libre Converter
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<byte[]> response = restTemplate.exchange(endpointUrl, HttpMethod.POST, 
																requestEntity, byte[].class);
		
		try (FileOutputStream fileOutputStream = new FileOutputStream(tempPdfFile)) {
			fileOutputStream.write(response.getBody());
		}

		return tempPdfFile;
	}

}
