/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

/**
 * 
 */
public interface GeneratePdfService {

	/**
	 * @param html
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	String generatePdfFromHtml(String html, String fileName) throws IOException;

	/**
	 * @param html
	 * @param fileName
	 * @return
	 */
	ResponseEntity<Map<String, Object>> publishDocument(String html, Long appInstId, Integer startPageNumber);

}
