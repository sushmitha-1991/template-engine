package com.dsp.templateEngine.translation;

import java.io.File;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RegionalTranslationDto {
	
	// Input Params from Publish module
	String fileUrl;
	String houseAbbr;
	String documentType;
	String documentSubType;
	LocalDate sittingDate;
	boolean editFlag;
	
	// Application instance ID for logging
	Long applicationInstanceId;
	
	// Input Params from Publish module --> converted to End Point params
	File source;
	String category;
	String subCategory;
	String type;
}
