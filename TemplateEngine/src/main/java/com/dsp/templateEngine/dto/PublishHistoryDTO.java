package com.dsp.templateEngine.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.dsp.templateEngine.config.XSSConfig.AllowHtml;
import com.dsp.templateEngine.config.XSSConfig.NoEscape;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishHistoryDTO {

	private Long id;
	private Long ballotingId;
	private String lokSabhaNo;
	private String lokSabhaSessionNo;
	private String rajyaSabhaSessionNo;

	private Long applicationId;
	private String applicationName;
	private String applicationNo;

	private UUID publishUuid;
	@NoEscape
	private String publishUrl;
	private String fileType;
	private String locale;

	private LocalDate forDate;
	private Boolean publishStatus;

	private String publishByUser;
	private LocalDate publishDate;

	private String unpublishByUser;
	private LocalDate unpublishDate;

	private Long officeId;
	@AllowHtml
	private String htmlContent;
	private String house;
	private int year;
	private int month;
	private String noticeType;
}
