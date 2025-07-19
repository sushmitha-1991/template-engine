/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "only_office_metadata", schema = "master")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlyOfficeMetadata extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "application_id")
	private Long applicationId;

	@Column(name = "document_key")
	private String documentKey;

	@Column(name = "download_url")
	private String downloadUrl;

	@Column(name = "only_office_url")
	private String onlyOfficeUrl;

	@Column(name = "dsp2_key")
	private String dsp2Key;

}