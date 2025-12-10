package com.dsp.templateEngine.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity to store regional language translation API request and response logs
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "regional_translation_log", schema = "template")
public class RegionalTranslationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "regional_translation_log_seq")
	@SequenceGenerator(name = "regional_translation_log_seq", sequenceName = "template.regional_translation_log_seq", allocationSize = 1)
	private Long id;

	@Column(name = "application_instance_id")
	private Long applicationInstanceId;

	@Column(name = "response_body", columnDefinition = "TEXT")
	private String responseBody;

	@Column(name = "response_status", length = 50)
	private String responseStatus;

	@Column(name = "sitting_date")
	private LocalDate sittingDate;

	@Column(name = "document_type")
	private String documentType;

	@Column(name = "document_sub_type")
	private String documentSubType;

	@Column(name = "file_url", columnDefinition = "TEXT")
	private String fileUrl;

	@Column(name = "edit_flag")
	private Boolean editFlag;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
