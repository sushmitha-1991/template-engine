package com.dsp.templateEngine.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.dsp.templateEngine.converter.ConverterFactory;
import com.dsp.templateEngine.converter.IConverter;
import com.dsp.templateEngine.converter.PdfConverterDTO;
import com.dsp.templateEngine.dto.ApplicationInstanceProjection;
import com.dsp.templateEngine.dto.MemberDTO;
import com.dsp.templateEngine.dto.OnlyOfficeData;
import com.dsp.templateEngine.dto.PublishHistoryDTO;
import com.dsp.templateEngine.dto.UploadedDocumentDTO;
import com.dsp.templateEngine.dto.UploadedDocumentProjection;
import com.dsp.templateEngine.entity.ApplicationInstance;
import com.dsp.templateEngine.entity.BaseUser;
import com.dsp.templateEngine.entity.PublishHistoryEntity;
import com.dsp.templateEngine.entity.WorkflowHistory;
import com.dsp.templateEngine.exception.OpenKMException;
import com.dsp.templateEngine.exception.ResourceNotFoundException;
import com.dsp.templateEngine.model.GeneratedPdfModel;
import com.dsp.templateEngine.repository.ApplicationInstanceRepository;
import com.dsp.templateEngine.repository.ApplicationRepository;
import com.dsp.templateEngine.repository.GeneratedPdfModelRepository;
import com.dsp.templateEngine.repository.MyUserRepository;
import com.dsp.templateEngine.repository.PublishRepository;
import com.dsp.templateEngine.repository.WorkflowHistoryRepository;
import com.dsp.templateEngine.service.Dsp1CacheService;
import com.dsp.templateEngine.service.FolderStructureService;
import com.dsp.templateEngine.service.GeneratePdfService;
import com.dsp.templateEngine.service.TranslationService;
import com.dsp.templateEngine.util.Dsp1Utils;
import com.dsp.templateEngine.util.JwtUtil;
import com.dsp.templateEngine.util.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeneratePdfServiceImpl implements GeneratePdfService {

	@Autowired
	Environment environment;

	@Autowired
	private FolderStructureService folderService;
	@Autowired
	private TranslationService translationService;

	@Value("${workflow.service.url}")
	private String draftedServiceUrl;

	@Value("${dsp1.cache.clear.url}")
	private String dsp1CacheClearUrl;

	@Value("${upload.document.url}")
	private String uploadDocUrl;

	@Autowired
	private Dsp1CacheService dsp1CacheService;

	@Autowired
	private ApplicationInstanceRepository applicationInstanceRepository;

	@Autowired
	private GeneratedPdfModelRepository generatedPdfModelRepository;

	@Autowired
	private Utils util;

	@Value("${openKm.service.upload.endpoint}")
	private String openKmUploadEndpoint;

	@Value("${openKm.service.download.endpoint}")
	private String openKmDownloadEndPoint;

	@Value("${openKm.dsp1.download.endpoint}")
	private String openKmDsp1DownloadEndPoint;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MyUserRepository userResponsibility;

	@Autowired
	private PublishRepository publishRepository;

	@Autowired
	private Utils utils;

	@Autowired
	private Dsp1Utils dsp1Utils;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private ConverterFactory factory;

	@Autowired
	private WorkflowHistoryRepository workflowHistoryRepository;

	@Autowired
	private ApplicationRepository appRepository;

	private final String SUMMON = "SN";

	private final String PROROGATION = "PN";

	private static final String LISTOFBUSINESS = "LOB";

	private static final String TRANSLATION_ABBR = "TRP";
	
	private static final String SYNOPSIS_ABBR = "SYM";

	private static final String CREATION_OF_NOTICES_UNDER_RULE_377 = "N377";

	public String generatePdfFromHtml(String html, String fileName) throws IOException {
		Path finalPath = null;
		try {
			String SERVER_PATH = "C:\\\\Users\\\\LQ917SL\\\\TestPdf";
			GeneratedPdfModel pdfModel = new GeneratedPdfModel();
			Path uploadPath = Paths.get(SERVER_PATH).toAbsolutePath().normalize();

			Scanner s = new Scanner(html).useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			finalPath = uploadPath.resolve(fileName);

			Path temp = uploadPath.resolve("temp.pdf");

			String FONTS = "src/main/resources/font/";
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(temp.toString()));
			ConverterProperties properties = new ConverterProperties();
			FontProvider fontProvider = new DefaultFontProvider();
			fontProvider.addDirectory(FONTS);
			properties.setFontProvider(fontProvider);

			String filePath = "C:\\Test\\temp.pdf";
			String filePathDest = "C:\\Test\\temp1.pdf";

			HtmlConverter.convertToPdf(result, out, properties);

			log.info("PDF path:" + SERVER_PATH + "/" + fileName);

			String fileList = "";
			// manipulatePdf(finalPath.toString());
			// addPageNumberingAndWatermark(src, dest, null, true, true);

		} catch (Exception e) {
			log.error("encountered exception", e);
		}
		return finalPath.toString();
	}

	@Transactional
	public ResponseEntity<Map<String, Object>> publishDocument(String htmlContentEn, String htmlContentHi,
			Long appInstId, Integer startPageNumber, UUID uploadedDoc) {
		try {
			if (isAlreadyPublished(appInstId)) {
				return buildAlreadyPublishedResponse(appInstId);
			}

			ApplicationInstanceProjection appDetails = getAppDetails(appInstId);

			String ooDocUrl = null; // Only Office Document URL, if exists

			OnlyOfficeData ooData = util.getODTDataIfExists(appInstId);
			if (ooData != null && !StringUtils.isEmpty(ooData.getDocUrl()))
				ooDocUrl = ooData.getDocUrl();

			UUID documentUuidEn = null;
			UUID documentUuidHi = null;

			// Check for Translation Papers Publishing
			String appAbbr = applicationInstanceRepository.getApplicationNameByInstId(appInstId);
			if (appAbbr.equals(TRANSLATION_ABBR)) {
//				htmlContentHi = htmlContentEn;
				htmlContentEn = null;
			}
			
			//SYNOPSIS
			String hindiDocUrl="";
			if(appAbbr.equals(SYNOPSIS_ABBR) && !StringUtils.isBlank(htmlContentHi)) {
				OnlyOfficeData ooDataSyn = util.getODTDataForSynopsis(appInstId);
				if (ooDataSyn != null && !StringUtils.isEmpty(ooDataSyn.getDocUrl()))
					hindiDocUrl = ooDataSyn.getDocUrl();
				System.out.println(hindiDocUrl);
			}
			
			if (uploadedDoc != null) {
				documentUuidEn = uploadedDoc;
			} else {
				if (!StringUtils.isBlank(htmlContentEn)) {
					documentUuidEn = generateAndUploadPdf(htmlContentEn, startPageNumber, appDetails, appInstId, "_en",
							ooData.getDocUrl());
				}
				if (!StringUtils.isBlank(htmlContentHi) && appAbbr.equals(SYNOPSIS_ABBR)) {
					System.out.println("Here in the synopsis block for hindi");
					documentUuidHi = generateAndUploadPdf(htmlContentHi, startPageNumber, appDetails, appInstId, "_hi",
							hindiDocUrl);
				} else if (!StringUtils.isBlank(htmlContentHi)){
					documentUuidHi = generateAndUploadPdf(htmlContentHi, startPageNumber, appDetails, appInstId, "_hi",
							ooData.getDocUrl());
				}
			}

			updatePublishedStatus(appInstId, documentUuidEn, documentUuidHi);
			// Integration touch point for Section Login -> MP Portal Letters/Documents
			if (appAbbr.equals(CREATION_OF_NOTICES_UNDER_RULE_377)) {
				try {
					saveUploadedDocuments(appInstId);
				} catch (Exception e) {
					log.error("Error saving uploaded documents for appInstId: " + appInstId, e);
				}
			}
			updateWorkflowHistory(appInstId);
			savePublishHistory(appInstId, appDetails, documentUuidEn, documentUuidHi, appAbbr);
			// Trigger async cache clearing — won't block publishing flow
			dsp1CacheService.clearCacheDsp1(appDetails.getAppNo(), appAbbr);
			//translation code
			
			Boolean isSendToTranslation=false;
			// try {
			// 	translationService.sendToTranslation(appInstId, appDetails, documentUuidEn, appAbbr);
			// 	isSendToTranslation = true;
			// } catch (Exception e) {
			// 	isSendToTranslation = false;
			// }

			return buildSuccessResponse(documentUuidEn, documentUuidHi,isSendToTranslation);
		} catch (Exception e) {
			log.error("Error publishing document for appInstId: " + appInstId, e);
			return buildErrorResponse(appInstId, e);
		}
	}

	private UploadedDocumentDTO saveUploadedDocuments(Long appInstId) {
		// Fetch house ID and application data
		UploadedDocumentProjection data = applicationInstanceRepository.getAppId(appInstId);

		// Prepare member data
		MemberDTO member = new MemberDTO();
		member.setMemberId(null);
		member.setName(null);
		member.setParty(null);
		member.setConstituency(null);
		member.setState(null);
		member.setIcNo(null);
		member.setMpCode(data.getMpCode());

		List<MemberDTO> membersData = List.of(member);

		// Build the DTO for API
		UploadedDocumentDTO payload = new UploadedDocumentDTO();
		payload.setId(null);
		payload.setDraftId(null);
		payload.setMembersData(membersData);
		payload.setTitle("Matter Under Rule 377");
		payload.setApplicationId(data.getAppId().toString());
		payload.setLokSabhaNumber(1);
		payload.setSessionNumber(99);
		payload.setDocumentUuid(data.getDocumentUuid());

		HttpEntity<UploadedDocumentDTO> entity = new HttpEntity<>(payload);

		try {
			UploadedDocumentDTO response = restTemplate.postForObject(uploadDocUrl, entity, UploadedDocumentDTO.class);
			log.info(">>> API response: " + response);
			return response;
		} catch (Exception e) {
			log.error("Error during API call Uploaded docu: " + e.getMessage());
			log.error("Error in API call", e);
			throw e;
		}
	}

	private boolean isAlreadyPublished(Long appInstId) {
		return Boolean.TRUE.equals(generatedPdfModelRepository.isApplicationInstancePublished(appInstId));
	}

	private ResponseEntity<Map<String, Object>> buildAlreadyPublishedResponse(Long appInstId) {
		log.info("Document is already published for appInstId: {}", appInstId);
		Map<String, Object> response = Map.of("statusCode", 409, "success", false, "message",
				"The document for this application instance is already published.", "data", null);
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	private ApplicationInstanceProjection getAppDetails(Long appInstId) {
		log.error("app id " + appInstId);
		ApplicationInstanceProjection details = generatedPdfModelRepository.findApplicationDetailsById(appInstId);
		if (details == null) {
			throw new IllegalArgumentException("Application instance not found for ID: " + appInstId);
		}
		return details;
	}

	private UUID generateAndUploadPdf(String htmlContent, Integer startPageNumber,
			ApplicationInstanceProjection appDetails, Long appInstId, String langSuffix, String ooDocUrl)
			throws Exception {

		String fileName = appDetails.getAppNo() + langSuffix + ".pdf";
		log.info("**** appDetails " + appDetails);

		String okmPath = "/okm:root/" + appDetails.getAbbr();
		log.info("appDetails " + appDetails);

		List<String> folders = new ArrayList();
//		//changes 19-07-2025
		String houseAbbr = utils.getHouseAbbr();
		folders.add(houseAbbr);
		if ("LS".equals(houseAbbr)) {
			folders.add(appDetails.getLsNo());
			folders.add(appDetails.getLsSesNo());
		} else {
			folders.add(appDetails.getRsSesNo());
		}
		folders.add(appDetails.getAbbr());
		// new chages
		log.info("folder " + folders);
		okmPath = folderService.createDmsFolderStructure(folders);
		// Get Generator Engine
		log.info("okm path " + okmPath);

		log.info("app ABBR: " + appDetails.getAbbr());

		Boolean ooFlag = !StringUtils.isEmpty(ooDocUrl);
		IConverter converter = factory.getConverter(appDetails.getAbbr(), langSuffix, ooFlag);

		PdfConverterDTO input = PdfConverterDTO.builder().htmlContent(htmlContent).fileName(fileName)
				.startPageNumber(startPageNumber).docUrl(ooDocUrl).build();

		File pdfFile = converter.convertToPdf(input);

		String uploadResult = uploadPdfToOpenKM(pdfFile, fileName, okmPath, "231", appInstId.toString(),
				"ApplicationInstance", "1");

		return extractUuidFromOpenKmResponse(uploadResult);
	}

	private UUID extractUuidFromOpenKmResponse(String uploadResult) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> responseMap = objectMapper.readValue(uploadResult, new TypeReference<>() {
		});

		if (responseMap == null || !responseMap.containsKey("data")) {
			throw new IllegalStateException("Invalid response: 'data' key is missing.");
		}

		Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

		if (data == null || !data.containsKey("documentUuid") || data.get("documentUuid") == null) {
			throw new OpenKMException("Invalid OpenKM response: Missing 'documentUuid'");
		}

		return UUID.fromString(data.get("documentUuid").toString());
	}

	private void updatePublishedStatus(Long appInstId, UUID documentUuid, UUID documentUuidHi) {
		generatedPdfModelRepository.updateApplicationInstanceStatusToPublishedEn(appInstId, documentUuid,
				documentUuidHi);
	}

	private void updateWorkflowHistory(Long appInstId) {
		Long workflowHistoryId = generatedPdfModelRepository.getWorkflowIdByAppInstanceId(appInstId);
		cloneAndPublishWorkflowHistory(workflowHistoryId);
	}

	private PublishHistoryDTO buildPublishHistoryDto(String locale, String houseAbbr, ApplicationInstance instance,
			String applicationName, UUID uuid) {
		return PublishHistoryDTO.builder().lokSabhaNo(instance.getLokSabhaSession())
				.lokSabhaSessionNo(instance.getLokSabhaSessionNumber())
				.rajyaSabhaSessionNo(instance.getRajyaSabhaSession()).applicationId(instance.getApplicationId())
				.applicationNo("hi".equals(locale) ? instance.getApplicationNo() + "_HI" : instance.getApplicationNo())
				.applicationName(applicationName).publishUuid(uuid).publishUrl(openKmDsp1DownloadEndPoint + "/" + uuid)
				.publishStatus(true).publishDate(LocalDate.now()).house(houseAbbr).locale(locale)
				.forDate(instance.getDocumentDate()).fileType("pdf").build();
	}

	private void savePublishHistory(Long appInstId, ApplicationInstanceProjection appDetails, UUID documentUuid,
			UUID documentUuidHi, String appAbbr) {

		String token = utils.getTokenFromRequest();

		if (token == null) {
			throw new IllegalStateException("Authorization token is missing");
		}
		String houseAbbr = jwtUtil.extractHouseAbbr(token);
		ApplicationInstance instance = applicationInstanceRepository.findById(appInstId)
				.orElseThrow(() -> new EntityNotFoundException("ApplicationInstance not found for ID: " + appInstId));
		String applicationName = appDetails.getAppName();
		if (StringUtils.isBlank(applicationName))
			applicationName = appDetails.getAbbr();

		// Fetch translation document type ID (if available)
		Long translAppId = applicationInstanceRepository.getTranslationFlag(appInstId);

		// Decide which applicationId to use
		Long applicationId = translAppId != null ? translAppId : instance.getApplicationId();

		// Decide which application name to use
		String applicationNameToUse = translAppId != null
				? applicationInstanceRepository.getApplicationName(translAppId)
				: applicationName; // fallback to default

		PublishHistoryDTO publishHistoryDto = PublishHistoryDTO.builder().lokSabhaNo(instance.getLokSabhaSession())
				.lokSabhaSessionNo(instance.getLokSabhaSessionNumber()).locale("en").house(houseAbbr)
				.rajyaSabhaSessionNo(instance.getRajyaSabhaSession()).applicationId(applicationId)
				.applicationNo(instance.getApplicationNo()).applicationName(applicationNameToUse)
				.publishUuid(documentUuid).publishUrl(openKmDsp1DownloadEndPoint + "/" + documentUuid)
				.publishStatus(true).publishDate(LocalDate.now()).forDate(instance.getDocumentDate()).fileType("pdf")
				.build();

		PublishHistoryDTO dtoHi = PublishHistoryDTO.builder().lokSabhaNo(instance.getLokSabhaSession())
				.lokSabhaSessionNo(instance.getLokSabhaSessionNumber())
				.rajyaSabhaSessionNo(instance.getRajyaSabhaSession()).applicationId(applicationId)
				.applicationNo(instance.getApplicationNo() + "_HI").applicationName(applicationNameToUse)
				.publishUuid(documentUuidHi).publishUrl(openKmDsp1DownloadEndPoint + "/" + documentUuidHi)
				.publishStatus(true).publishDate(LocalDate.now()).house(houseAbbr).locale("hi")
				.forDate(instance.getDocumentDate()).fileType("pdf").build();

		if (SUMMON.equals(appDetails.getAbbr()) || PROROGATION.equals(appDetails.getAbbr())) {
			// dsp1Utils.writeToDSP1(appDetails, publishHistoryDto, dtoHi, houseAbbr);
			dsp1Utils.writeToDSP1Lss(appDetails, publishHistoryDto, dtoHi, houseAbbr);
		}
		if (LISTOFBUSINESS.equalsIgnoreCase(appDetails.getAbbr())) {
			String dsp1AppName = dsp1Utils.getLobApplicationName(publishHistoryDto, instance,
					applicationInstanceRepository);
			publishHistoryDto.setApplicationName(dsp1AppName);

			dtoHi.setApplicationName(dsp1AppName);
		}

		if (!TRANSLATION_ABBR.equalsIgnoreCase(appDetails.getAbbr())) {
			addPublishHistoryEntry(publishHistoryDto);
		}

		if (documentUuidHi != null) {
			addPublishHistoryEntry(dtoHi);
		}
	}

	@Override
	public String savedSuccess(Long appInstId, UUID documentUuid, UUID documentUuidHi) {
		ApplicationInstanceProjection appDetails = getAppDetails(appInstId);
		try {
			savePublishHistory(appInstId, appDetails, documentUuid, documentUuidHi, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "saved";
	}

	private ResponseEntity<Map<String, Object>> buildSuccessResponse(UUID enUuid, UUID hiUuid,Boolean isSendToTranslation) {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "Document(s) published successfully.");
		response.put("englishDocumentUuid", enUuid);
		response.put("hindiDocumentUuid", hiUuid);
		response.put("isSendToTranslation",isSendToTranslation);
		return ResponseEntity.ok(response);
	}

	private ResponseEntity<Map<String, Object>> buildErrorResponse(Long appInstId, Exception e) {
		Map<String, Object> error = Map.of("statusCode", 500, "success", false, "message",
				"Failed to publish document for appInstId: " + appInstId + ". Error: " + e.getMessage(), "data", null);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Transactional
	public void cloneAndPublishWorkflowHistory(Long workflowHistoryId) {
		Optional<WorkflowHistory> optionalHistory = workflowHistoryRepository.findById(workflowHistoryId);

		Long userId = utils.getUserIdFromAuthentication();
		String username = workflowHistoryRepository.getUserName(userId);

		if (optionalHistory.isEmpty()) {
			throw new ResourceNotFoundException("WorkflowHistory not found for ID: " + workflowHistoryId);
		}

		WorkflowHistory originalHistory = optionalHistory.get();

		// Clone the original entity
		WorkflowHistory clonedHistory = new WorkflowHistory();
		BeanUtils.copyProperties(originalHistory, clonedHistory, "id"); // Exclude ID to create a new
		// record

		// Update the cloned history's status to "Published"
		clonedHistory.setStatus("Published");
		clonedHistory.setActionTakenOn(LocalDateTime.now());
		clonedHistory.setActionTakenBy(userId);
		clonedHistory.setActionTakenByName(username);

		// Update the original record
		originalHistory.setIsDeleted(true);
		originalHistory.setIsActive(false);

		// Save both records
		workflowHistoryRepository.save(originalHistory);
		workflowHistoryRepository.save(clonedHistory);
	}

	public File generatePdfFromHtmlOpenKm(String html, String fileName) throws IOException {
		try {
			// Define the directory where the PDF will be stored
			String SERVER_PATH = "C:\\Users\\LQ917SL\\TestPdf";
			Path uploadPath = Paths.get(SERVER_PATH).toAbsolutePath().normalize();

			// Ensure the upload directory exists
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Define the final path for the PDF file
			Path finalPath = uploadPath.resolve(fileName);

			// Convert HTML to a PDF
			String FONTS = "src/main/resources/font/";
			ConverterProperties properties = new ConverterProperties();
			FontProvider fontProvider = new DefaultFontProvider();
			fontProvider.addDirectory(FONTS);
			properties.setFontProvider(fontProvider);

			try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalPath.toString()))) {
				Scanner s = new Scanner(html).useDelimiter("\\A");
				String result = s.hasNext() ? s.next() : "";
				HtmlConverter.convertToPdf(result, out, properties);
			}

			log.info("PDF generated at path: " + finalPath.toString());

			// Return the generated File
			return finalPath.toFile();
		} catch (Exception e) {
			log.error("Error generating PDF", e);
			throw new RuntimeException("Failed to generate PDF", e);
		}
	}

	public String uploadPdfToOpenKM(File pdfFile, String documentName, String folderPath, String masterId,
			String referenceId, String referenceType, String applicationNo) {
		try {
			// The JWT token is automatically added by the RestTemplate interceptor
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			// Create a FileSystemResource from the file
			FileSystemResource fileResource = new FileSystemResource(pdfFile);
			// Prepare the body with all required fields
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("folderPath", folderPath);
			body.add("documentName", documentName);
			body.add("file", fileResource);
			body.add("masterId", masterId);
			body.add("referenceId", referenceId);
			body.add("referenceType", referenceType);
			body.add("applicationNo", applicationNo);

			System.out.println("### body template - " + body.toString());

			// Prepare the request entity with headers and body
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			System.out.println("Request data for upload doc");
			System.out.println(requestEntity);
			// String openKmUrl = "https://dsptest.sansad.in/dms/openkm/document/upload";
			ResponseEntity<String> response = restTemplate.exchange(openKmUploadEndpoint, HttpMethod.POST,
					requestEntity, String.class);

			// Return the response body as a string
			return response.getBody();
		} catch (HttpClientErrorException e) {
			log.error("Error in HTTP request to OpenKM: ", e);
			throw new RuntimeException("Error uploading document to OpenKM. HTTP Error: " + e.getMessage());
		} catch (Exception e) {
			log.error("Error uploading PDF to OpenKM", e);
			throw new RuntimeException("Error uploading document to OpenKM.", e);
		}
	}

	public File convertHtmlToPdf(String htmlContent, String fileName, Integer startPageNumber) throws IOException {
		File tempPdfFile = File.createTempFile(fileName, ".pdf");

		String fontDirectory = "src/main/resources/font/";
		ConverterProperties converterProperties = new ConverterProperties();
		FontProvider fontProvider = new DefaultFontProvider();
		fontProvider.addDirectory(fontDirectory);
		converterProperties.setFontProvider(fontProvider);

		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		HtmlConverter.convertToPdf(htmlContent, pdfOutputStream, converterProperties);

		byte[] numberedPdfBytes = addPageNumbersToPdf(pdfOutputStream.toByteArray(), startPageNumber);

		try (FileOutputStream fileOutputStream = new FileOutputStream(tempPdfFile)) {
			fileOutputStream.write(numberedPdfBytes);
		}

		return tempPdfFile;
	}

	public byte[] addPageNumbersToPdf(byte[] pdfBytes, Integer startPageNumber) throws IOException {
		int startingPage = (startPageNumber != null) ? startPageNumber : 1;

		ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfBytes);
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

		PdfReader reader = new PdfReader(pdfInputStream);
		PdfWriter writer = new PdfWriter(pdfOutputStream);
		PdfDocument pdfDoc = new PdfDocument(reader, writer);
		Document document = new Document(pdfDoc);
		int totalPages = pdfDoc.getNumberOfPages();

		// Load Tahoma font from resources or file system
		// Replace this path with actual location if needed
		PdfFont tahomaFont = PdfFontFactory.createFont("src/main/resources/font/tahoma.ttf");

		for (int i = 1; i <= totalPages; i++) {
			document.showTextAligned(
					new Paragraph(String.valueOf(startingPage + i - 1)).setFontSize(10).setFont(tahomaFont), 297.5f, // X
																														// position
																														// (A4
																														// center)
					15, // Y position (bottom)
					i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
		}

		document.close();
		return pdfOutputStream.toByteArray();
	}

	@Override
	public byte[] mergePdfs(byte[] enPdfBytes, byte[] hiPdfBytes) throws IOException {
		ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
		PdfDocument mergedPdf = new PdfDocument(new PdfWriter(mergedOutputStream));

		if (enPdfBytes != null && enPdfBytes.length > 0) {
			PdfDocument enPdf = new PdfDocument(new PdfReader(new ByteArrayInputStream(enPdfBytes)));
			enPdf.copyPagesTo(1, enPdf.getNumberOfPages(), mergedPdf);
			enPdf.close();
		}

		if (hiPdfBytes != null && hiPdfBytes.length > 0) {
			PdfDocument hiPdf = new PdfDocument(new PdfReader(new ByteArrayInputStream(hiPdfBytes)));
			hiPdf.copyPagesTo(1, hiPdf.getNumberOfPages(), mergedPdf);
			hiPdf.close();
		}

		mergedPdf.close();
		return mergedOutputStream.toByteArray();
	}

	@Override
	public void createPdfV3(PdfConverterDTO upload, java.io.ByteArrayOutputStream outputStream) {

		if (StringUtils.isEmpty(upload.getHtmlContent()) && StringUtils.isEmpty(upload.getDocUrl())) {
			return;
		}

		System.out.println("########### appInstId::" + upload.getAppInstanceId());

		String abbr = upload.getApplicationAbbr();
		if (StringUtils.isEmpty(abbr) && upload.getAppInstanceId() != null) {
			ApplicationInstanceProjection appDetails = getAppDetails(upload.getAppInstanceId());
			abbr = appDetails != null ? appDetails.getAbbr() : null;
			System.out.println("########### abbr::" + abbr);
			System.out.println("########### abbr::" + appDetails);
		}

		boolean odtFlag = !StringUtils.isEmpty(upload.getDocUrl());
		IConverter converter = factory.getConverter(abbr, upload.getLocale(), odtFlag);
		log.debug(converter.toString());

		try {
			converter.convertToByteArray(upload, outputStream);
		} catch (IOException e) {
			log.error(e.getMessage());
			return;
		}
	}

	private PublishHistoryDTO addPublishHistoryEntry(PublishHistoryDTO publishHistoryDto) {
		String url = draftedServiceUrl + "/publish-history/create";
		// String url = "http://localhost:8087/workflow/publish-history/create";

		String token = utils.getTokenFromRequest();
		if (token == null) {
			throw new IllegalStateException("Authorization token is missing");
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		String sub = jwtUtil.extractUsername(token); // Retrieves "sub"
		String houseAbbr = jwtUtil.extractHouseAbbr(token);
		BaseUser userDetails = userResponsibility.findByUsername(sub);
		System.out.println("user Data " + userDetails);
		Long officeId = generatedPdfModelRepository.getOfficeIdUsingUserId(userDetails.getId());
		publishHistoryDto.setPublishByUser(
				userDetails.getFirstName() + " " + userDetails.getMiddleName() + " " + userDetails.getLastName());
		publishHistoryDto.setOfficeId(officeId);
		HttpEntity<PublishHistoryDTO> entity = new HttpEntity<>(publishHistoryDto);

		try {
			return restTemplate.postForObject(url, entity, PublishHistoryDTO.class);
		} catch (Exception e) {
			System.err.println("Error during API call: " + e.getMessage());
			log.error("Error in Api Call", e);
			throw e;
		}
	}

	@Override
	public PublishHistoryDTO publishNotice(PublishHistoryDTO dto) {
		try {
			// Step 2: Fetch application details
			String abbr = generatedPdfModelRepository.findAbbrByApplicationId(dto.getApplicationId());
			String applicationName = generatedPdfModelRepository.findDSPAppNameByApplicationId(dto.getApplicationId());
			if (abbr == null) {
				throw new IllegalArgumentException("Application instance not found for ID: " + dto.getApplicationId());
			}

			Random random = new Random();
			int randomInt = random.nextInt();
			// Generate filename with the language suffix
			String appNo = "Balloting" + randomInt + ".pdf";
			String ballotTitle = "Balloting" + randomInt;
			String okmPath = "/okm:root/" + abbr;

			// Step 2: Convert HTML content to a PDF
			File pdfFile = convertHtmlToPdf(dto.getHtmlContent(), appNo, 1);

			// Step 3: Upload PDF to OpenKM
			String uploadResult = uploadPdfToOpenKM(pdfFile, appNo, okmPath, "231", dto.getApplicationId().toString(),
					"ApplicationInstance", "1");
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(uploadResult, new TypeReference<>() {
			});

			if (responseMap == null || !responseMap.containsKey("data")) {
				throw new IllegalStateException("Invalid response: 'data' key is missing.");
			}

			Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

			if (data == null || !data.containsKey("documentUuid") || data.get("documentUuid") == null) {
				throw new OpenKMException("Invalid OpenKM response: Missing 'documentUuid'");
			}
			UUID documentUuid = UUID.fromString(data.get("documentUuid").toString());
			// Assuming the method should return some DTO after processing
			String publishedUrl = openKmDsp1DownloadEndPoint + "/" + documentUuid;

			String token = utils.getTokenFromRequest();
			if (token == null) {
				throw new IllegalStateException("Authorization token is missing");
			}
			String houseAbbr = jwtUtil.extractHouseAbbr(token);

			PublishHistoryEntity entry = PublishHistoryEntity.builder().lokSabhaNo(dto.getLokSabhaNo())
					.lokSabhaSessionNo(dto.getLokSabhaSessionNo()).applicationId(dto.getApplicationId())
					.applicationName(applicationName).applicationNo(appNo).publishUuid(documentUuid)
					.publishByUser(dto.getPublishByUser()).publishUrl(publishedUrl).publishDate(LocalDate.now())
					.fileType("pdf").locale(dto.getLocale()).forDate(dto.getForDate())
					.publishStatus(dto.getPublishStatus()).unpublishByUser(dto.getUnpublishByUser())
					.unpublishDate(dto.getUnpublishDate()).house(houseAbbr).build();
			entry = publishRepository.save(entry);

			// Writing to DSP1
			try {
				dsp1Utils.writeToBallotingDSP1(entry, houseAbbr, ballotTitle);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error in writeToBallDSP1", e);
			}

			return dto;
		} catch (IOException e) {
			e.printStackTrace(); // Log the exception
			throw new RuntimeException("Error while processing notice publishing", e);
		}
	}

	@Override
	public UUID uploadHtmlAndReturnUuid(String htmlContent, int startingPageNo, Long applicationId, String locale,
			String fileName) {

		PdfConverterDTO upload = PdfConverterDTO.builder().htmlContent(htmlContent).startPageNumber(startingPageNo)
				.fileName(fileName).applicationId(applicationId).locale(locale).build();

		return uploadHtmlAndReturnUuid(upload);

	}

	@Override
	public UUID uploadHtmlAndReturnUuid(PdfConverterDTO upload) {

		if (upload == null)
			return null;

		try {

			// Step 1: Generate a random file name, if not specified
			String fileName = getFileName(upload);

			// Step 2: Convert content to PDF
			String locale = StringUtils.isEmpty(upload.getLocale()) ? "en" : upload.getLocale();

			if (upload.getApplicationId() != null && StringUtils.isEmpty(upload.getApplicationAbbr())) {
				// get application abbr
				System.out.println("APPLICATION ID: " + upload.getApplicationId());
				String abbr = appRepository.findAbbrById(upload.getApplicationId());
				System.out.println("APPLICATION ABBR: " + abbr);

				if (!StringUtils.isEmpty(abbr))
					upload.setApplicationAbbr(abbr);
			}

			IConverter converter = factory.getConverter(upload.getApplicationAbbr(), locale, false);
			File pdfFile = converter.convertToPdf(upload);

			if (pdfFile == null || !pdfFile.exists()) {
				throw new IOException("Failed to generate PDF from HTML content.");
			}

			// Step 3: Upload to OpenKM
			String okmPath = Utils.OKM_ROOT;
			String uploadResult = uploadPdfToOpenKM(pdfFile, fileName, okmPath, "231",
					upload.getApplicationId().toString(), "ApplicationInstance", "1");

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(uploadResult, new TypeReference<>() {
			});
			if (responseMap == null || !responseMap.containsKey("data")) {
				throw new IllegalStateException("Invalid response: 'data' key is missing.");
			}

			Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
			if (data == null || !data.containsKey("documentUuid") || data.get("documentUuid") == null) {
				throw new OpenKMException("Invalid OpenKM response: Missing 'documentUuid'");
			}

			// Step 4: Return the document UUID
			return UUID.fromString(data.get("documentUuid").toString());

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while processing HTML to PDF upload", e);
		}
	}

	private String getFileName(PdfConverterDTO upload) {

		if (!StringUtils.isEmpty(upload.getFileName()))
			return upload.getFileName();

		// Else Generate a random file name
		String fileName = UUID.randomUUID() + "_" + upload.getLocale() + ".pdf";
		upload.setFileName(fileName);
		return fileName;
	}
	
	@Transactional
	public ResponseEntity<Map<String, Object>> publishDocumentV2(String htmlContentEn, String htmlContentHi,

			Long appInstId, Integer startPageNumber, UUID uploadedDoc) {

		try {

			if (isAlreadyPublished(appInstId)) {

				return buildAlreadyPublishedResponse(appInstId);

			}

			ApplicationInstanceProjection appDetails = getAppDetails(appInstId);

			String appAbbr = appDetails.getAbbr();

			OnlyOfficeData ooData = util.getODTDataIfExistsV2(appInstId);

			PublishDTO docDto = null;

			if (uploadedDoc != null) {

				docDto = PublishDTO.builder().documentUuidEn(uploadedDoc).documentUuidHi(null).build();

			} else if (ooData != null && ooData.getDocUrl() != null) {

				docDto = processOODocument(startPageNumber, appDetails, ooData);

			}

			else {

				// It is an HTML document

				docDto = processHTMLDocument(htmlContentEn, htmlContentHi, startPageNumber, appDetails);

			}

			UUID documentUuidEn = docDto.getDocumentUuidEn();

			UUID documentUuidHi = docDto.getDocumentUuidHi();

			updatePublishedStatus(appInstId, documentUuidEn, documentUuidHi);

			// Integration touch point for Section Login -> MP Portal Letters/Documents

			if (appAbbr.equals(CREATION_OF_NOTICES_UNDER_RULE_377)) {

				try {

					saveUploadedDocuments(appInstId);

				} catch (Exception e) {

					log.error("Error saving uploaded documents for appInstId: " + appInstId, e);

				}

			}

			updateWorkflowHistory(appInstId);

			savePublishHistory(appInstId, appDetails, documentUuidEn, documentUuidHi, appAbbr);

			// Trigger async cache clearing — won't block publishing flow

			dsp1CacheService.clearCacheDsp1(appDetails.getAppNo(), appAbbr);

			//translation code
			
			Boolean isSendToTranslation=false;
			// try {
			// 	translationService.sendToTranslation(appInstId, appDetails, documentUuidEn, appAbbr);
			// 	isSendToTranslation = true;
			// } catch (Exception e) {
			// 	isSendToTranslation = false;
			// }


			return buildSuccessResponse(documentUuidEn, documentUuidHi,isSendToTranslation);

		} catch (Exception e) {

			log.error("Error publishing document for appInstId: " + appInstId, e);

			return buildErrorResponse(appInstId, e);

		}

	}

	private PublishDTO processOODocument(int startPageNumber,

			ApplicationInstanceProjection appDetails,

			OnlyOfficeData ooData) throws Exception {

		PublishDTO docDto = null;

		String appAbbr = appDetails.getAbbr();

		if (appAbbr.equals(TRANSLATION_ABBR)) { // process Translation

			docDto = processTranslation(startPageNumber, appDetails, ooData);

		} else if (appAbbr.equals(SYNOPSIS_ABBR)) { // process Synopsis

			docDto = processSynopsis(startPageNumber, appDetails, ooData);

		} else { // any other OO module

			docDto = processOOApp(startPageNumber, appDetails, ooData);

		}

		return docDto;

	}

	private PublishDTO processTranslation(int startPageNumber,

			ApplicationInstanceProjection appDetails, OnlyOfficeData ooData) throws Exception {

		UUID documentUuidHi = generateDoc(null, startPageNumber, appDetails, "_hi", ooData.getDocUrl());

		return PublishDTO.builder().documentUuidEn(null).documentUuidHi(documentUuidHi).build();

	}

	private PublishDTO processSynopsis(int startPageNumber,

			ApplicationInstanceProjection appDetails, OnlyOfficeData ooData) throws Exception {

		UUID documentUuidEn = null;

		UUID documentUuidHi = null;

		String htmlContent = null;

		if (!StringUtils.isEmpty(appDetails.getOnlyOfficeDsp2Key())) {

			documentUuidEn = generateDoc(htmlContent, startPageNumber, appDetails, "_en", ooData.getDocUrl());

		}

		if (!StringUtils.isEmpty(appDetails.getDsp2KeyHi())) {

			documentUuidHi = generateDoc(htmlContent, startPageNumber, appDetails, "_hi", ooData.getDocUrlHi());

		}

		return PublishDTO.builder().documentUuidEn(documentUuidEn).documentUuidHi(documentUuidHi).build();

	}

	private PublishDTO processOOApp(int startPageNumber,

			ApplicationInstanceProjection appDetails, OnlyOfficeData ooData) throws Exception {

		UUID documentUuidEn = null;

		UUID documentUuidHi = null;

		String htmlContent = null;

		if (!StringUtils.isEmpty(appDetails.getOnlyOfficeDsp2Key())) {

			documentUuidEn = generateDoc(htmlContent, startPageNumber, appDetails, "_en", ooData.getDocUrl());

		}

		if (!StringUtils.isEmpty(appDetails.getDsp2KeyHi())) {

			documentUuidHi = generateDoc(htmlContent, startPageNumber, appDetails, "_hi", ooData.getDocUrlHi());

		}

		return PublishDTO.builder().documentUuidEn(documentUuidEn).documentUuidHi(documentUuidHi).build();

	}

	private PublishDTO processHTMLDocument(String htmlContentEn, String htmlContentHi, int startPageNumber,

			ApplicationInstanceProjection appDetails) throws Exception {

		UUID documentUuidEn = null;

		UUID documentUuidHi = null;

		if (!StringUtils.isEmpty(htmlContentEn)) {

			documentUuidEn = generateDoc(htmlContentEn, startPageNumber, appDetails, "_en", null);

		}

		if (!StringUtils.isEmpty(htmlContentHi)) {

			documentUuidHi = generateDoc(htmlContentHi, startPageNumber, appDetails, "_hi", null);

		}

		return PublishDTO.builder().documentUuidEn(documentUuidEn).documentUuidHi(documentUuidHi).build();

	}

	private UUID generateDoc(String htmlContent, int startPageNumber,

			ApplicationInstanceProjection appDetails, String locale,String docUrl) throws Exception {

		UUID documentUuid = generateAndUploadPdf(htmlContent, startPageNumber, appDetails, appDetails.getAppId(),
				locale, docUrl);

		return documentUuid;

	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Setter
	@Builder
	public static class PublishDTO {
		private UUID documentUuidEn;
		private UUID documentUuidHi;

	}
}
