/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.dto;

/**
 * 
 */
public interface PublishHistoryProjection {
	String getLokSabhaNo();

	String getLokSabhaSessionNo();

	String getRajyaSabhaSessionNo();

	String getApplicationNo();

	Long getApplicationId();

	String getLocale();

	String getYear();

	String getMonth();

}
