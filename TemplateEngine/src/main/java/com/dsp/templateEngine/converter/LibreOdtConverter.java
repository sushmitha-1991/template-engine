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
	
	
	public File convertToPdf (PdfConverterDTO input) throws IOException {
		
		// Extract relevant fields
		String fileName = input.getFileName();
		Integer startPageNumber = input.getStartPageNumber();
		String contentUrl = input.getDocUrl();
		
		return convertOdtToPdf (contentUrl, fileName); 
	}
	
	
	public File convertOdtToPdf (String downloadUrl, String fileName) throws IOException {
	
		// Step 1: Prepare JSON payload to call /convert-odt endpoint
		String endpointUrl = libreServiceUrl + LIBRE_ODT_END_POINT;

		System.out.println(">>> inside converter");

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("url", downloadUrl);

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

		// Step 2: Call /convert-odt endpoint to get PDF bytes
		ResponseEntity<byte[]> pdfResponse = restTemplate.exchange(endpointUrl, HttpMethod.POST, requestEntity,
				byte[].class);

		if (!pdfResponse.getStatusCode().is2xxSuccessful() || pdfResponse.getBody() == null) {
			throw new IOException("Failed to convert ODT to PDF via Libre service");
		}

		byte[] pdfBytes = pdfResponse.getBody();

		// Step 3: Write PDF bytes to a temp file and return it
		File tempPdfFile = File.createTempFile(fileName, ".pdf");
		try (FileOutputStream fos = new FileOutputStream(tempPdfFile)) {
			fos.write(pdfBytes);
		}

		return tempPdfFile;
	}	

	
//	public void convertOdtToPdf(String docUrl, String fileName, ByteArrayOutputStream outputStream) {
//		try {
//			System.out.println(">>> inside convertOdt");
//			
//			File file = convertOdtFromUrlToPdfFile(docUrl, fileName);
//			
//			try (FileInputStream fis = new FileInputStream(file)) {
//				byte[] buffer = new byte[8192];
//				int bytesRead;
//				while ((bytesRead = fis.read(buffer)) != -1) {
//					outputStream.write(buffer, 0, bytesRead);
//				}
//			}
//		} catch (IOException e) {
//			throw new RuntimeException("ODT to PDF conversion failed", e);
//		}
//	}

	
	public File convertHtmlToPdf(String htmlContent, String fileName, Integer startPageNumber) throws IOException {

		throw new UnsupportedOperationException("Not supported for Libre ODT Converter");

	}

	public void convertHtmlToPdfByteArray(String htmlContent, ByteArrayOutputStream outputStream)  {

		throw new UnsupportedOperationException("Not supported for Libre ODT Converter");

	}
}
