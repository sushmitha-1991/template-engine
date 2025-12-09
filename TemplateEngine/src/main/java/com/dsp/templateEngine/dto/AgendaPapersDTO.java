/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.dto;

import java.time.LocalDate;

/**
 * 
 */
public interface AgendaPapersDTO {

	String getAppAbbr();

	Long getAppId();

	LocalDate getDate();

	String getAppNo();
}
