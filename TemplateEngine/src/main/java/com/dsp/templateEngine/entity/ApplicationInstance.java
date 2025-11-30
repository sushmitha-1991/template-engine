package com.dsp.templateEngine.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.dsp.templateEngine.config.XSSConfig.AllowHtml;
import com.dsp.templateEngine.config.XSSConfig.NoEscape;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "application_instance", schema = "master")
public class ApplicationInstance extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_instance_seq")
	@SequenceGenerator(name = "application_instance_seq", sequenceName = "master.application_instance_seq", allocationSize = 1)
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "application_no", nullable = false, length = 255, unique = true)
	private String applicationNo;

	@Column(name = "ref_id")
	private Long refId;

	@Column(name = "current_status", nullable = false, length = 100)
	private String currentStatus;

	@NoEscape
	@Column(name = "published_document_url", length = 255)
	private String publishedDocumentUrl;

	@Column(name = "process_instance_id", length = 255)
	private String processInstanceId;

	@Column(name = "task_id", length = 255)
	private String taskId;

	@Column(name = "is_published")
	private Boolean isPublished;

	@Column(name = "lok_sabha_session", length = 30)
	private String lokSabhaSession;

	@Column(name = "lok_sabha_session_number", length = 30)
	private String lokSabhaSessionNumber;

	@Column(name = "rajya_sabha_session", length = 30)
	private String rajyaSabhaSession;

	@Column(name = "additional_details", columnDefinition = "text")
	private String additionalDetails;

	@Column(name = "draft")
	private Boolean draft = true;

	@Column(name = "document_date")
	private LocalDate documentDate;

	@Column(name = "published_document_uuid_en")
	private UUID publishedDocumentUuidEn;

	@Column(name = "published_document_uuid_hi")
	private UUID publishedDocumentUuidHi;

	@AllowHtml
	@Column(name = "html_content_en", columnDefinition = "text")
	private String htmlContentEnglish;

	@AllowHtml
	@Column(name = "html_content_hi", columnDefinition = "text")
	private String htmlContentHindi;

	@Column(name = "uploaded_document_uuid")
	private UUID uploadedDocumentUuid;

	@Column(name = "starting_page_no")
	private Long startingPageNo;

	@Column(name = "parent_instance_id")
	private Long parentInstanceId;

	@Column(name = "translation_document_type_id")
	private Long translationDocumentTypeId;
	
	@Column(name = "document_display_name")
	private String documentDisplayName;
}
