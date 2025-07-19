package com.dsp.templateEngine.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.templateEngine.dto.PublishHistoryDTO;
import com.dsp.templateEngine.dto.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;
import com.dsp.templateEngine.response.GenericResponse;
import com.dsp.templateEngine.service.GeneratePdfService;
import com.dsp.templateEngine.service.NewEventsLssService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/template")
public class GeneratedPdfController {

	@Autowired
	private GeneratePdfService pdfService;
	@Autowired
	private NewEventsLssService nelssService;

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
	public ResponseEntity<byte[]> downloadHtmlFromMyTask(@RequestParam String htmlContentEn,
			@RequestParam(required = false) String htmlContentHi, @RequestParam String fileName) {
		try {
			// Normalize htmlContentHi: Handle cases where it's received as "null" (string)
			// or empty
			if ("undefined".equalsIgnoreCase(htmlContentHi) || "null".equalsIgnoreCase(htmlContentHi)
					|| htmlContentHi == null || htmlContentHi.trim().isEmpty()) {
				htmlContentHi = null;
			}

			// Generate the English PDF content
			ByteArrayOutputStream enPdfStream = null;
			if (htmlContentEn != null) {
				enPdfStream = new ByteArrayOutputStream();
				pdfService.createPdfFromHtmlV2(htmlContentEn, enPdfStream, "en");
			}

			// Generate Hindi PDF only if htmlContentHi is present
			ByteArrayOutputStream hiPdfStream = null;
			if (htmlContentHi != null) {
				hiPdfStream = new ByteArrayOutputStream();
				pdfService.createPdfFromHtmlV2(htmlContentHi, hiPdfStream, "hi");
			}

			// Merge PDFs only if Hindi content exists, otherwise use English PDF as final output
			byte[] mergedPdfBytes;

			if (enPdfStream != null && hiPdfStream != null) {
				mergedPdfBytes = pdfService.mergePdfs(enPdfStream.toByteArray(), hiPdfStream.toByteArray());
			} else {
				mergedPdfBytes = enPdfStream != null ? enPdfStream.toByteArray() : hiPdfStream.toByteArray();
			}

			// Add page numbers to the final PDF
			byte[] pdfWithPageNumbers = pdfService.addPageNumbersToPdf(mergedPdfBytes, 1);

			// Set HTTP response headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", fileName + ".pdf");

			return new ResponseEntity<>(pdfWithPageNumbers, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error generating PDF: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/publish-document")
	public ResponseEntity<GenericResponse<Map<String, Object>>> publishDocument(@RequestParam String htmlContentEn,
			@RequestParam(required = false) String htmlContentHi, @RequestParam Long appInstId,
			@RequestParam(required = false, defaultValue = "1") Integer startPageNumber,
			@RequestParam(required = false) UUID uploadedDoc) {

		log.info("Publishing document for appInstId: {} with startPageNumber: {}", appInstId, startPageNumber);

		try {
			ResponseEntity<Map<String, Object>> response = pdfService.publishDocument(htmlContentEn, htmlContentHi,
					appInstId, startPageNumber, uploadedDoc);

			if (response.getStatusCode() == HttpStatus.CONFLICT) {
				return handleConflict(response);
			}

			Map<String, Object> responseBody = response.getBody();
			Map<String, Object> resultData = new HashMap<>();
			if (responseBody != null) {
				resultData.put("englishDocumentUuid", responseBody.get("englishDocumentUuid"));
				resultData.put("hindiDocumentUuid", responseBody.get("hindiDocumentUuid"));
			}

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

	@PostMapping("/balloting/publish")
	public GenericResponse<PublishHistoryDTO> save(@RequestBody PublishHistoryDTO dto) {
		pdfService.publishNotice(dto);

		return new GenericResponse<>(200, true, "Balloting published successfully", null);
	}

	
	@PostMapping(value = "/upload-html-english", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadHtml(@RequestParam String htmlContentEn, 
										@RequestParam int startingPageNo,
										@RequestParam Long applicationId,
										@RequestParam (defaultValue = "en", required=false) String locale,
										@RequestParam (required=false) String fileName) {

		try {
			UUID documentUuid = pdfService.uploadHtmlAndReturnUuid(htmlContentEn, startingPageNo, applicationId, locale, fileName);

			Map<String, Object> response = new HashMap<>();
			response.put("documentUuid", documentUuid.toString());

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> error = new HashMap<>();
			error.put("statusCode", 500);
			error.put("success", false);
			error.put("message", "Failed to upload PDF: " + e.getMessage());
			error.put("documentUuid", null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@PostMapping("/update-dsp1-table")
	public ResponseEntity<NewEventsLss> addDsp1Table(@RequestBody NewEventsLssDTO dto) {
		NewEventsLss response = nelssService.saveNewEvent(dto);
		return ResponseEntity.ok(response);
	}
}
