package com.dsp.templateEngine.converter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class LibreHtmlConverter implements IConverter {

	@Value("${libre.service.url}")
	private String libreServiceUrl;
	
	private static final String LIBRE_END_POINT = "/convert"; 
	
	
	public File convertToPdf (PdfConverterDTO input) throws IOException {
		
		File tempPdfFile = File.createTempFile(input.getFileName(), ".pdf");
		
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		convertToByteArray(input, pdfOutputStream);
		
		try (FileOutputStream fileOutputStream = new FileOutputStream(tempPdfFile)) {
			fileOutputStream.write(pdfOutputStream.toByteArray());
		}

		return tempPdfFile;
	}
	
	
	@Override
	public void convertToByteArray(PdfConverterDTO input, ByteArrayOutputStream outputStream) throws IOException {

		String endpointUrl = libreServiceUrl + LIBRE_END_POINT;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("text/html; charset=UTF-8"));
		headers.set("Accept-Language", "hi-IN");
		
		// Prepare the request entity with headers and body
		HttpEntity<String> requestEntity = new HttpEntity<>(input.getHtmlContent(), headers);

		// Use Rest Template to call Libre Converter
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<byte[]> response = restTemplate.exchange(endpointUrl, HttpMethod.POST, 
																requestEntity, byte[].class);

		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new IOException("Failed to convert HTML to PDF via Libre service");
		}
		
		outputStream.write(response.getBody(), 0, response.getBody().length);
	}


}
