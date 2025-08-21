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
public interface UploadedDocumentProjection {

	String getAppName();

	Long getLsNo();

	Long getLsSesNo();

	Long getAppId();

	String getMpCode();

	UUID getDocumentUuid();

	LocalDate getNoticeDate();

}