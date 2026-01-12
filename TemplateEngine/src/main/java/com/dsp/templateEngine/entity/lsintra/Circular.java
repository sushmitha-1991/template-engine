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
@Table(name = "tlb_circular", schema = "dbo", catalog = "intra_circular")
public class Circular {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", nullable = false, precision = 18, scale = 0)
	private BigDecimal id;

	@Column(name = "circular_date", nullable = false)
	private LocalDateTime circularDate;

	@Column(name = "circular_type", precision = 18, scale = 0)
	private BigDecimal circularType;

	@Column(name = "circular_no", length = 50)
	private String circularNo;

	@Column(name = "circular_sub", length = 4000)
	private String circularSub;

	@Column(name = "filepath", length = 1000)
	private String filepath;

	@Column(name = "lastupdate", nullable = false, length = 50)
	private String lastupdate;

	@Column(name = "branchid")
	private Integer branchId;

	@Column(name = "ip_addr", length = 50)
	private String ipAddr;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	@Column(name = "published_by", length = 255)
	private String publishedBy;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "created_by", length = 255)
	private String createdBy;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "updated_by", length = 255)
	private String updatedBy;

	@Column(name = "sub_catid")
	private Integer subCatId;

	@Column(name = "status", length = 50)
	private String status;

	@Column(name = "keyword", length = 4000)
	private String keyword;

	@Column(name = "uuid", length = 250)
	private String uuid;
}
