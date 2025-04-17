/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.dsp.templateEngine.dto.PublishHistoryDTO;

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
	 * @param startPageNumber
	 * @param uploadedDoc 
	 * @param fileName
	 * @return
	 */
	ResponseEntity<Map<String, Object>> publishDocument(String htmlContentEn, String htmlContentHi, Long appInstId,
			Integer startPageNumber, UUID uploadedDoc);

	void createPdfFromHtml(String htmlContent, ByteArrayOutputStream outputStream) throws IOException;

	byte[] mergePdfs(byte[] enPdfBytes, byte[] hiPdfBytes) throws IOException;

	byte[] addPageNumbersToPdf(byte[] pdfBytes, Integer startPageNumber) throws IOException;

	/**
	 * @param dto
	 * @return
	 */
	PublishHistoryDTO publishNotice(PublishHistoryDTO dto);

}
