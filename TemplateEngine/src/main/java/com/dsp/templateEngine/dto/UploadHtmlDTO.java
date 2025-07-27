package com.dsp.templateEngine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadHtmlDTO {

	String htmlContent;
	String fileName;
	int startingPageNo;
	Long applicationId;
	Long appInstanceId;
	String locale;
	String applicationAbbr;
}
