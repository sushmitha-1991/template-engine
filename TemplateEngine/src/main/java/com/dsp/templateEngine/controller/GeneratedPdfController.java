package com.dsp.templateEngine.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
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

import com.dsp.templateEngine.converter.PdfConverterDTO;
import com.dsp.templateEngine.dto.OnlyOfficeData;
//import com.dsp.templateEngine.dto.OnlyOfficeData;
import com.dsp.templateEngine.dto.PublishHistoryDTO;
import com.dsp.templateEngine.dto.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.ApplicationInstance;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;
import com.dsp.templateEngine.exception.ResourceNotFoundException;
import com.dsp.templateEngine.repository.ApplicationInstanceRepository;
import com.dsp.templateEngine.repository.OnlyOfficeMetadataRepository;
//import com.dsp.templateEngine.repository.OnlyOfficeMetadataRepository;
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

	@Autowired
	private ApplicationInstanceRepository applicationInstanceRepository;

	@Autowired
	private OnlyOfficeMetadataRepository onlyOfficeMetadataRepository;

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
	public ResponseEntity<byte[]> downloadHtmlFromMyTask(@RequestParam(required = false) String htmlContentEn,
			@RequestParam(required = false) String htmlContentHi, @RequestParam String fileName,
			@RequestParam(required = false) Long applicationInstanceId) {

		PdfConverterDTO input = PdfConverterDTO.builder().fileName(fileName).appInstanceId(applicationInstanceId)
				.build();

		initPdfConversion(input);

		try {
			ByteArrayOutputStream enPdfStream = null;
			ByteArrayOutputStream hiPdfStream = null;

			if (input.getDocUrl() != null) {
				enPdfStream = processODT(input);
			} else {

				// Normalize htmlContentHi: Handle cases where it's received as "null" (string)
				// or empty
				if ("undefined".equalsIgnoreCase(htmlContentHi) || "null".equalsIgnoreCase(htmlContentHi)
						|| htmlContentHi == null || htmlContentHi.trim().isEmpty()) {
					htmlContentHi = null;
				}

				input.setLocale("en");
				input.setHtmlContent(htmlContentEn);
				enPdfStream = processHtml(input);
				System.out.println("Generated En: " + enPdfStream);

				if (htmlContentHi != null) {
					input.setHtmlContent(htmlContentHi);
					input.setLocale("hi");
					hiPdfStream = processHtml(input);
				}
			}
			// Merge PDFs only if Hindi content exists, otherwise use English PDF as final
			// output
			byte[] mergedPdfBytes = null;

			if (enPdfStream != null && hiPdfStream != null) {
				mergedPdfBytes = pdfService.mergePdfs(enPdfStream.toByteArray(), hiPdfStream.toByteArray());
			} else {
				if (enPdfStream != null)
					mergedPdfBytes = enPdfStream.toByteArray();
				if (hiPdfStream != null)
					mergedPdfBytes = hiPdfStream.toByteArray();
			}

			System.out.println("mergedPdfBytes: " + mergedPdfBytes);

			// Add page numbers to the final PDF
			byte[] pdfWithPageNumbers = pdfService.addPageNumbersToPdf(mergedPdfBytes, input.getStartPageNumber());

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
			@RequestParam(required = false) String uploadedDoc) {

		log.info("Publishing document for appInstId: {} with startPageNumber: {}", appInstId, startPageNumber);

		try {

			Integer startingPageNumber = 1;
			if (appInstId != null) {
				ApplicationInstance instance = applicationInstanceRepository.findById(appInstId).orElseThrow(
						() -> new ResourceNotFoundException("ApplicationInstance not found with id: " + appInstId));

				if (instance.getStartingPageNo() != null) {
					startingPageNumber = instance.getStartingPageNo().intValue();
				}
			}

			// ✅ Convert uploadedDoc only if valid
			UUID uploadedDocUuid = null;
			if (uploadedDoc != null && !uploadedDoc.trim().isEmpty() && !"null".equalsIgnoreCase(uploadedDoc)) {
				uploadedDocUuid = UUID.fromString(uploadedDoc);
			}

			ResponseEntity<Map<String, Object>> response = pdfService.publishDocument(htmlContentEn, htmlContentHi,
					appInstId, startingPageNumber, uploadedDocUuid);

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

	private void initPdfConversion(PdfConverterDTO input) {

		Integer startingPageNumber = 1;
		Long applicationInstanceId = input.getAppInstanceId();

		if (input.getAppInstanceId() != null) {
			ApplicationInstance instance = applicationInstanceRepository.findById(applicationInstanceId)
					.orElseThrow(() -> new ResourceNotFoundException(
							"ApplicationInstance not found with id: " + applicationInstanceId));

			if (instance.getStartingPageNo() != null) {
				startingPageNumber = instance.getStartingPageNo().intValue();
			}
		}
		input.setStartPageNumber(startingPageNumber);

		// Check if Document is in ODT Format
		OnlyOfficeData ooData = getODTDataIfExists(applicationInstanceId);
		System.out.println("ooData: " + ooData);

		if (ooData != null && !StringUtils.isEmpty(ooData.getDocUrl())) {
			input.setDocUrl(ooData.getDocUrl());
			return;
		}

	}

	private OnlyOfficeData getODTDataIfExists(Long appInstId) {

		OnlyOfficeData onlyOfficeData = onlyOfficeMetadataRepository.getOnlyOfficeData(appInstId);
		return onlyOfficeData;

	}

	private ByteArrayOutputStream processHtml(PdfConverterDTO input) {

		System.out.println("### processHtml HTML - ");
		System.out.println("### INPUT - " + input);

		ByteArrayOutputStream pdfStream = null;
		if (input.getHtmlContent() != null) {
			pdfStream = new ByteArrayOutputStream();
			pdfService.createPdfV3(input, pdfStream);
		}
		return pdfStream;
	}

	private ByteArrayOutputStream processODT(PdfConverterDTO input) {

		System.out.println("### processODT - ");

		ByteArrayOutputStream pdfStream = null;
		if (input.getDocUrl() != null) {
			pdfStream = new ByteArrayOutputStream();
			pdfService.createPdfV3(input, pdfStream);
		}
		return pdfStream;
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
	public ResponseEntity<?> uploadHtml(@RequestParam String htmlContentEn, @RequestParam int startingPageNo,
			@RequestParam Long applicationId, @RequestParam(defaultValue = "en", required = false) String locale,
			@RequestParam(required = false) String fileName, @RequestParam(required = false) String applicationAbbr) {

		try {

			PdfConverterDTO upload = PdfConverterDTO.builder().htmlContent(htmlContentEn)
					.startPageNumber(startingPageNo).applicationId(applicationId).applicationAbbr(applicationAbbr)
					.locale(locale).fileName(fileName).build();

			UUID documentUuid = pdfService.uploadHtmlAndReturnUuid(upload);

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

//	@PostMapping(value = "/upload-html-english", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<?> uploadHtml(@RequestBody PdfConverterDTO upload) {
//
//		try {
//
//			if (upload == null) {
//				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//			}
//			
//			UUID documentUuid = pdfService.uploadHtmlAndReturnUuid(upload);
//
//			Map<String, Object> response = new HashMap<>();
//			response.put("documentUuid", documentUuid.toString());
//
//			return ResponseEntity.ok(response);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			Map<String, Object> error = new HashMap<>();
//			error.put("statusCode", 500);
//			error.put("success", false);
//			error.put("message", "Failed to upload PDF: " + e.getMessage());
//			error.put("documentUuid", null);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//		}
//	}

	@PostMapping("/update-dsp1-table")
	public ResponseEntity<NewEventsLss> addDsp1Table(@RequestBody NewEventsLssDTO dto) {
		NewEventsLss response = nelssService.saveNewEvent(dto);
		return ResponseEntity.ok(response);
	}
}
