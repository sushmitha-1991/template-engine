package com.dsp.templateEngine.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(schema = "template", name = "generate_pdf_model")
public class GeneratedPdfModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // or GenerationType.AUTO
	private Long id; // This will be your primary key

	private Long refId;

	private String refType;

	private Integer version;

	private String html;

	private String pdfPath;
}
