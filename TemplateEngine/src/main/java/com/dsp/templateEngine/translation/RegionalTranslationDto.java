package com.dsp.templateEngine.translation;

import java.io.File;
import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class RegionalTranslationDto {
	
	// Input Params from Publish module
	String fileUrl;
	String houseAbbr;
	String documentType;
	String documentSubType;
	LocalDate sittingDate;
	boolean editFlag;
	
	// Input Params from Publish module --> converted to End Point params
	File source;
	String category;
	String subCategory;
	String type;
}
