package com.dsp.templateEngine.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.dsp.templateEngine.dto.RegionalTranslationLogDto;
import com.dsp.templateEngine.entity.RegionalTranslationLog;
import com.dsp.templateEngine.repository.RegionalTranslationLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
			LocalDate sittingDate, String documentType, String documentSubType, String fileUrl, boolean editFlag, Boolean isSendManual) 
										throws JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String status = rootNode.get("status").toString();
        
		try {
			RegionalTranslationLog translationLog = RegionalTranslationLog.builder()
					.applicationInstanceId(applicationInstanceId)
					.responseBody(response != null ? response.getBody() : null)
					//.responseStatus(response != null ? response.getStatusCode().toString() : "UNKNOWN")
					.responseStatus(status).isSendManually(isSendManual)
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

	public List<RegionalTranslationLogDto> getTranslationLog(Long appInstId, LocalDate date, String responseStatus,
		String docType, String subDocType, Boolean edit, LocalDate fromDate, LocalDate toDate, String type,Boolean manuallyTranslated)
	{
        Specification<RegionalTranslationLog> spec = Specification.where(null);
		if(edit != null)
		{
			spec = spec.and(isEdit(edit));
		}
		if(date != null)
		{
			spec = spec.and(hasDate(date));
		}
		if(fromDate != null ||  toDate != null)
		{
			spec = spec.and(hasDateBetween(fromDate, toDate));
		}
		if(StringUtils.isNotBlank(docType))
		{
			spec = spec.and(hasDocType(docType));
		}
		if(StringUtils.isNotBlank(subDocType))
		{
			spec = spec.and(hasDocSubType(subDocType));
		}
		if(appInstId != null)
		{
			spec = spec.and(hasAppInstance(appInstId));
		}
		if(manuallyTranslated != null)
		{
			spec = spec.and(isSendManually(manuallyTranslated));
		}
		return mapToDto(repository.findAll(spec));
		// return new ArrayList<>();
	}
	public List<RegionalTranslationLogDto> mapToDto(List<RegionalTranslationLog> entity)
	{
		return entity.stream()
				.map(e->
				{
					RegionalTranslationLogDto dto = RegionalTranslationLogDto.builder()
							.responseBody(e.getResponseBody())
							.responseStatus(e.getResponseStatus())
							.sittingDate(e.getSittingDate())
							.editFlag(e.getEditFlag())
							.documentType(e.getDocumentType())
							.documentSubType(e.getDocumentSubType())
							.build();
					return dto;
				})
				.collect(Collectors.toList());
	}
	public Specification<RegionalTranslationLog> isSendManually(Boolean isSendManually)
	{
		return (root, query, criteriaBuilder) ->
		 criteriaBuilder.equal(root.get("isSendManually"), isSendManually);
	}

	public Specification<RegionalTranslationLog> isEdit(Boolean edit)
	{
		return (root, query, criteriaBuilder) ->
		 criteriaBuilder.equal(root.get("editFlag"), edit);
	}

	public Specification<RegionalTranslationLog> hasAppInstance(Long appInstId)
	{
		return (root, query, criteriaBuilder) ->
		 criteriaBuilder.equal(root.get("applicationInstanceId"), appInstId);
	}

	public static Specification<RegionalTranslationLog> hasDocType(String docType) {
		return (root, query, cb) -> {
			if (docType == null || docType.trim().isEmpty()) {
				return cb.conjunction();
			}
			return cb.like(
					cb.lower(root.get("documentType")),
					"%" + docType.toLowerCase() + "%"
			);
		};
	}

	public static Specification<RegionalTranslationLog> hasDocSubType(String docSubType) {
		return (root, query, cb) -> {
			if (docSubType == null || docSubType.trim().isEmpty()) {
				return cb.conjunction();
			}
			return cb.like(
					cb.lower(root.get("documentSubType")),
					"%" + docSubType.toLowerCase() + "%"
			);
		};
	}

	public Specification<RegionalTranslationLog> hasDate(LocalDate sittingDate)
	{
		return (root, query, criteriaBuilder) ->
		 criteriaBuilder.equal(root.get("sittingDate"), sittingDate);
	}
	
    public static Specification<RegionalTranslationLog> hasDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> 
        {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("sittingDate"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("sittingDate"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("sittingDate"), endDate);
            } else {
                return null;
            }
        };
    }


}
