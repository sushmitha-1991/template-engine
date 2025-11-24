package com.dsp.template.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="e_transmission",schema="transmission")
public class ETransmission  extends Auditable{
	private Long id;

	    @Column(name = "lok_sabha_no")
	    private Long lokSabhaNo;

	    @Column(name = "lok_sabha_session_no")
	    private Long lokSabhaSessionNo;

	    @Column(name = "rajya_sabha_session_no")
	    private Long rajyaSabhaSessionNo;
	    
	    @Column(name = "office_paper_id")
	    private Long officepaperid;

	    @Column(name = "date_of_circulation", nullable = false)
	    private LocalDate dateOfCirculation;

	    @Column(name = "office_id", nullable = false)
	    private Long officeId;

		@Column(name = "file_english")
	    private UUID fileEnglish;

	    @Column(name = "file_english_ver", nullable = false)
	    private String fileEnglishVer;

	    @Column(name = "file_hindi")
	    private UUID fileHindi;

	    @Column(name = "file_hindi_ver", nullable = false)
	    private String fileHindiVer;
	    
	    @Column(name = "file_english_update")
	    private UUID file_english_update;
	    
	    @Column(name = "file_hindi_update")
	    private UUID file_hindi_update;

	    @Column(name = "errata_file_english")
	    private UUID errataFileEnglish;
	    
	    @Column(name = "errata_file_hindi")
	    private UUID errataFileHindi;
	    
	    @Column(name = "content")
	    private String content;
	    
	    @Column(name = "category_id")
	    private Integer categoryId;
	    
	    @Column(name = "date_of_sitting", nullable = false)
	    private LocalDate dateOfSitting;	    
}
