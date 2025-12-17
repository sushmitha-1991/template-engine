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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "applications", schema = "master")
public class Application extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_seq")
	@SequenceGenerator(name = "application_seq", sequenceName = "master.application_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false, length = 255)
	private String abbr;

	@Column(name = "dsp1_document_name")
	private String dsp1DocumentName;

	@Column(name = "document_display_name")
	private String documentDisplayName;

	@Column(name = "house")
	private String house;
}
