/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 
 */
public interface ApplicationInstanceProjection {

	String getAppNo();

	String getAbbr();

	String getAppName();

	String getLsNo();

	String getLsSesNo();

	String getRsSesNo();

	Long getAppId();

	String getMpCode();

	UUID getDocumentUuid();

	LocalDate getNoticeDate();
	
    String getOnlyOfficeDsp2Key();
	
	String getDsp2KeyHi();

}
