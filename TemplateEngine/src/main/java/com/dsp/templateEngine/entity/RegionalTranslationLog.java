package com.dsp.templateEngine.entity;

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

	@Column(name = "request_payload", columnDefinition = "TEXT")
	private String requestPayload;

	@Column(name = "response_body", columnDefinition = "TEXT")
	private String responseBody;

	@Column(name = "response_status", length = 50)
	private String responseStatus;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
