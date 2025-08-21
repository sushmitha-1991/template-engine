/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dsp.templateEngine.util.ListJsonConverter;
import com.dsp.templateEngine.util.LongListJsonConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

@Entity
@Table(name = "uploaded_document", schema = "section_login")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedDocument extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_login.uploaded_document_id_seq")
	@SequenceGenerator(name = "section_login.uploaded_document_id_seq", sequenceName = "section_login.uploaded_document_id_seq", allocationSize = 1)
	private Long id;

	@Column(name = "lok_sabha_number", length = 10)
	private String lokSabhaNumber;

	@Column(name = "session_number", length = 10)
	private String sessionNumber;

	@Column(name = "rajya_sabha_session", length = 10)
	private String rajyaSabhaSession;

	@Column(name = "house_id", nullable = false)
	private Long houseId;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Convert(converter = ListJsonConverter.class) // Using your custom converter
	@Column(name = "members_data", columnDefinition = "jsonb")
	private List<Map<String, Object>> membersData;

	@Column(name = "title", nullable = false, columnDefinition = "TEXT")
	private String title;

	@Column(name = "uploading_date", nullable = false)
	private LocalDateTime uploadingDate;

	@Column(name = "document_uuid") // Store as JSONB
	private UUID documentUuid;

	@Column(name = "office_name", columnDefinition = "TEXT")
	private String officeName;

	@Convert(converter = LongListJsonConverter.class)
	@Column(name = "office_id", columnDefinition = "jsonb")
	private List<Long> officeId;

	@Column(name = "receiver_id", nullable = false)
	private String receiverId;

	@Builder.Default
	@Column(name = "pin_flag", nullable = false, columnDefinition = "boolean default false")
	private boolean pinFlag = false;

	@Builder.Default
	@Column(name = "read_flag", nullable = false, columnDefinition = "boolean default false")
	private boolean readFlag = false;

}
