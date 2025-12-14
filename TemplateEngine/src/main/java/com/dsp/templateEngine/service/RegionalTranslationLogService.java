package com.dsp.templateEngine.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dsp.templateEngine.entity.RegionalTranslationLog;
import com.dsp.templateEngine.repository.RegionalTranslationLogRepository;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to save regional translation API request and response logs
 */
@Service
@Slf4j
public class RegionalTranslationLogService {

	@Autowired
	private RegionalTranslationLogRepository repository;

	/**
	 * Save translation request and response to database
	 * 
	 * @param applicationInstanceId the application instance ID
	 * @param requestBody the request body/payload sent to translate API
	 * @param response the response from translate API
	 */
	public void saveTranslationLog(Long applicationInstanceId, ResponseEntity<String> response,
			LocalDate sittingDate, String documentType, String documentSubType, String fileUrl, boolean editFlag) 
										throws JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String status = rootNode.get("status").toString();
        
		try {
			RegionalTranslationLog translationLog = RegionalTranslationLog.builder()
					.applicationInstanceId(applicationInstanceId)
					.responseBody(response != null ? response.getBody() : null)
					//.responseStatus(response != null ? response.getStatusCode().toString() : "UNKNOWN")
					.responseStatus(status)
					.sittingDate(sittingDate).documentType(documentType)
					.documentSubType(StringUtils.isEmpty(documentSubType) ? StringUtils.EMPTY : documentSubType)
					.fileUrl(fileUrl).editFlag(editFlag).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
					.build();

			repository.save(translationLog);
			log.info("Saved translation log for applicationInstanceId: {}", applicationInstanceId);
		} catch (Exception e) {
			log.error("Failed to save translation log for applicationInstanceId: {}. Error: {}", applicationInstanceId,
					e.getMessage());
		}
	}
}
