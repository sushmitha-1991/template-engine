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
@Table(name = "dsp1_cache_registry", schema = "template")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Dsp1CacheRegistry extends Auditable {

	@Id
	@SequenceGenerator(name = "dsp1_cache_registry_seq", sequenceName = "template.dsp1_cache_registry_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "template.dsp1_cache_registry_id_seq")
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "application_abbr", nullable = false, length = 100)
	private String applicationAbbr;

	@Column(name = "dsp1_cache_name", nullable = false, length = 100)
	private String dsp1CacheName;

	@Column(name = "dsp1_endpoint", nullable = false, length = 255)
	private String dsp1Endpoint;
}
