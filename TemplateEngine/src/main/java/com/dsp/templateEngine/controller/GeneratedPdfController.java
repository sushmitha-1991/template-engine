package com.dsp.templateEngine.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.templateEngine.response.GenericResponse;
import com.dsp.templateEngine.service.GeneratePdfService;

@RestController
@RequestMapping("/template")
public class GeneratedPdfController {

	@Autowired
	private GeneratePdfService pdfService;

	@PostMapping("/generatePdfFromHtml")
	public ResponseEntity<GenericResponse<String>> generatePdfFromHtml(@RequestParam String html,
			@RequestParam String fileName) {
		try {
			String pdfPath = pdfService.generatePdfFromHtml(html, fileName);
			GenericResponse<String> response = new GenericResponse<>(HttpStatus.OK.value(), true,
					"PDF generated successfully.", pdfPath);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (IOException e) {
			GenericResponse<String> response = new GenericResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
					"PDF generation failed.", null);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/publish-document")
	public ResponseEntity<GenericResponse<Map<String, Object>>> publishDocument(@RequestParam String html,
			@RequestParam Long appInstId, @RequestParam(required = false, defaultValue = "1") Integer startPageNumber) {
		// Call the service and get the response
		ResponseEntity<Map<String, Object>> serviceResponse = pdfService.publishDocument(html, appInstId,
				startPageNumber);

		// Extract the body and unwrap the nested "data" if it exists
		Map<String, Object> serviceBody = serviceResponse.getBody();
		Map<String, Object> unwrappedData = serviceBody != null && serviceBody.containsKey("data")
				? (Map<String, Object>) serviceBody.get("data")
				: null;

		// Determine success based on the status code
		boolean success = serviceResponse.getStatusCode().is2xxSuccessful();

		// Create the response object with the unwrapped data
		GenericResponse<Map<String, Object>> response = new GenericResponse<>(serviceResponse.getStatusCode().value(),
				success,
				serviceBody != null
						? serviceBody.getOrDefault("message", "Operation completed successfully.").toString()
						: "",
				unwrappedData);

		return new ResponseEntity<>(response, serviceResponse.getStatusCode());
	}

}
