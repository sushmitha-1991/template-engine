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
public class LibreOdtConverter implements IConverter {

	@Value("${libre.service.url}")
	private String libreServiceUrl;
	
	private static final String LIBRE_ODT_END_POINT = "/convert-odt"; 
	
	@Override
	public File convertToPdf (PdfConverterDTO input) throws IOException {
		
		// Extract relevant fields
		String fileName = input.getFileName();
	
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		convertToByteArray(input, pdfOutputStream);
	
		// Step 3: Write PDF bytes to a temp file and return it
		File tempPdfFile = File.createTempFile(fileName, ".pdf");
		try (FileOutputStream fileOutputStream = new FileOutputStream(tempPdfFile)) {
			fileOutputStream.write(pdfOutputStream.toByteArray());
		}

		return tempPdfFile;	
	}
	
	
//	public File convertOdtToPdf (PdfConverterDTO input) throws IOException {
//
//		byte[] pdfBytes = convertToByteArray(input);
//		// Step 3: Write PDF bytes to a temp file and return it
//		File tempPdfFile = File.createTempFile(fileName, ".pdf");
//		try (FileOutputStream fos = new FileOutputStream(tempPdfFile)) {
//			fos.write(pdfBytes);
//		}
//
//		return tempPdfFile;
//	}	

	
	@Override
	public void convertToByteArray(PdfConverterDTO input, ByteArrayOutputStream outputStream) throws IOException  {
		
		// Step 1: Prepare JSON payload to call /convert-odt endpoint
		String endpointUrl = libreServiceUrl + LIBRE_ODT_END_POINT;

		System.out.println(">>> inside converter");

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("url", input.getDocUrl());

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

		// Step 2: Call /convert-odt endpoint to get PDF bytes
		ResponseEntity<byte[]> response = restTemplate.exchange(endpointUrl, HttpMethod.POST, requestEntity,
				byte[].class);

		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new IOException("Failed to convert ODT to PDF via Libre service");
		}

//		byte[] pdfBytes = response.getBody();
		outputStream.write(response.getBody(), 0, response.getBody().length);
//		return pdfBytes;
	}	
	

}
