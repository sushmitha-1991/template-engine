package com.dsp.templateEngine.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.templateEngine.response.GenericResponse;
import com.dsp.templateEngine.service.GeneratePdfService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/template")
public class GeneratedPdfController {

	@Autowired
	private GeneratePdfService pdfService;

	@PostMapping("/generatePdfFromHtml")
	public ResponseEntity<GenericResponse<String>> generatePdfFromHtml(@RequestParam String html,
			@RequestParam String fileName) {
		log.info("Received request to generate PDF for file: {}", fileName);
		try {
			String pdfPath = pdfService.generatePdfFromHtml(html, fileName);
			log.info("PDF generated successfully for file: {}", fileName);
			GenericResponse<String> response = new GenericResponse<>(HttpStatus.OK.value(), true,
					"PDF generated successfully.", pdfPath);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (IOException e) {
			log.error("Error generating PDF for file: {}. Error: {}", fileName, e.getMessage());
			GenericResponse<String> response = new GenericResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
					"PDF generation failed.", null);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/mytask/download")
	public ResponseEntity<byte[]> downloadHtmlFromMyTask(@RequestParam String html, @RequestParam String fileName) {
		try {
			// Generate the PDF in memory
			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

			// Initialize the PDF writer and document
			PdfWriter writer = new PdfWriter(pdfStream);
			PdfDocument pdfDocument = new PdfDocument(writer);

			// Configure font provider for Hindi/Unicode support
			String fontDirectory = "src/main/resources/font/";
			ConverterProperties converterProperties = new ConverterProperties();
			FontProvider fontProvider = new DefaultFontProvider();
			fontProvider.addDirectory(fontDirectory); // Load all fonts from directory
			converterProperties.setFontProvider(fontProvider);

			// Convert HTML to PDF
			HtmlConverter.convertToPdf(html, pdfDocument, converterProperties);
			pdfDocument.close();

			// Prepare HTTP headers to trigger a download
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", fileName + ".pdf");

			// Return the generated PDF as a byte array in the response
			return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
		} catch (Exception e) {
			// Log error and return an error response
			log.error("Error generating PDF: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/publish-document")
	public ResponseEntity<GenericResponse<Map<String, Object>>> publishDocument(@RequestParam String htmlContentEn,
			@RequestParam(required = false) String htmlContentHi, @RequestParam Long appInstId,
			@RequestParam(required = false, defaultValue = "1") Integer startPageNumber) {
		log.info("Publishing document for appInstId: {} with startPageNumber: {}", appInstId, startPageNumber);

		try {
			ResponseEntity<Map<String, Object>> response = pdfService.publishDocument(htmlContentEn, htmlContentHi,
					appInstId, startPageNumber);

			if (response.getStatusCode() == HttpStatus.CONFLICT) {
				return handleConflict(response);
			}

			Map<String, Object> responseBody = response.getBody();
			Map<String, Object> resultData = new HashMap<>();
			resultData.put("documentUuid", responseBody != null ? responseBody.get("documentUuid") : null);

			log.info("Document published successfully for appInstId: {}", appInstId);

			GenericResponse<Map<String, Object>> genericResponse = new GenericResponse<>(HttpStatus.OK.value(), true,
					"Document published successfully.", resultData);

			return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error publishing document for appInstId: {}. Error: {}", appInstId, e.getMessage());
			GenericResponse<Map<String, Object>> errorResponse = new GenericResponse<>(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
					"Failed to publish document. Error: " + e.getMessage(), null);
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<GenericResponse<Map<String, Object>>> handleConflict(
			ResponseEntity<Map<String, Object>> conflictResponse) {
		Map<String, Object> responseBody = conflictResponse.getBody();
		GenericResponse<Map<String, Object>> conflictGenericResponse = new GenericResponse<>(HttpStatus.OK.value(),
				false, (responseBody != null) ? (String) responseBody.get("message") : "Conflict occurred.", null);
		return new ResponseEntity<>(conflictGenericResponse, HttpStatus.OK);
	}

}
