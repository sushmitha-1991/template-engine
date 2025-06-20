/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.entity;

import java.time.LocalDate;
import java.util.UUID;

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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "publish_history", schema = "workflow")
public class PublishHistoryEntity extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "publish_history_seq")
	@SequenceGenerator(name = "publish_history_seq", sequenceName = "workflow.publish_history_seq", allocationSize = 1)
	private Long id;

	@Column(name = "lok_sabha_no")
	private String lokSabhaNo;

	@Column(name = "lok_sabha_session_no")
	private String lokSabhaSessionNo;

	@Column(name = "rajya_sabha_session_no")
	private String rajyaSabhaSessionNo;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "application_name", nullable = false, length = 100)
	private String applicationName;

	@Column(name = "application_no", nullable = false, length = 100, unique = true)
	private String applicationNo;

	@Column(name = "publish_uuid", nullable = false)
	private UUID publishUuid;

	@Column(name = "publish_url", columnDefinition = "text", nullable = false)
	private String publishUrl;

	@Column(name = "for_date")
	private LocalDate forDate;

	@Column(name = "publish_status", nullable = false)
	private Boolean publishStatus;

	@Column(name = "publish_by_user", nullable = false, length = 100)
	private String publishByUser;

	@Column(name = "publish_date", nullable = false)
	private LocalDate publishDate;

	@Column(name = "unpublish_by_user")
	private String unpublishByUser;

	@Column(name = "unpublish_date")
	private LocalDate unpublishDate;

	@Column(name = "office_id")
	private Long officeId;

	@Column(name = "file_type", nullable = false)
	private String fileType;

	@Column(name = "locale")
	private String locale;

	@Column(name = "year")
	private String year;

	@Column(name = "month")
	private String month;

	@Column(name = "file_type_name")
	private String fileTypeName;

	@Column(name = "house")
	private String house;

	@Column(name = "title")
	private String title;
}
