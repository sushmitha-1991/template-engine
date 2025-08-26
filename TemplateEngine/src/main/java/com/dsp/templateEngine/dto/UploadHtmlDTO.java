package com.dsp.templateEngine.dto;

import com.dsp.templateEngine.config.XSSConfig.AllowHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadHtmlDTO {
	
	@AllowHtml
	String htmlContent;
	String fileName;
	int startingPageNo;
	Long applicationId;
	Long appInstanceId;
	String locale;
	String applicationAbbr;
}
