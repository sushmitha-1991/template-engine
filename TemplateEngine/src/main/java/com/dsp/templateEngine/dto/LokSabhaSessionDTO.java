/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LokSabhaSessionDTO {
	private int lsNo;
	private int sessionNo;
	private String session;
	private String loksabha;
}
