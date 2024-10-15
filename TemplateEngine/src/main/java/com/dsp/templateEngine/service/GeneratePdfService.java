/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.service;

import java.io.IOException;

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

}
