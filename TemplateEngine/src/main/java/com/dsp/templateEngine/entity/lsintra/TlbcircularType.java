package com.dsp.templateEngine.entity.lsintra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tlbcircular_type", schema = "dbo", catalog = "intra_circular")
public class TlbcircularType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, precision = 18, scale = 0)
	private BigDecimal id;

	@Column(name = "title", nullable = false, length = 150)
	private String title;

	@Column(name = "cat_short_name", length = 50)
	private String catShortName;

	@Column(name = "cat_lastsno")
	private Integer catLastSno;

	@Column(name = "published_by", length = 255)
	private String publishedBy;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "created_by", length = 255)
	private String createdBy;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "updated_by", length = 255)
	private String updatedBy;

	@Column(name = "branchid")
	private Integer branchId;

	@Column(name = "active_status")
	private Boolean activeStatus;

	@Column(name = "format", length = 1)
	private String format;
}
