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
import com.dsp.templateEngine.dto.UploadHtmlDTO;

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
	
	void createPdfFromHtmlV2(String htmlContent, ByteArrayOutputStream outputStream, String locale,  Long appInstId);

	byte[] mergePdfs(byte[] enPdfBytes, byte[] hiPdfBytes) throws IOException;

	byte[] addPageNumbersToPdf(byte[] pdfBytes, Integer startPageNumber) throws IOException;

	/**
	 * @param dto
	 * @return
	 */
	PublishHistoryDTO publishNotice(PublishHistoryDTO dto);

	/**
	 * @param htmlContentEn
	 * @param startingPageNo
	 * @param applicationId
	 * @return
	 */
	UUID uploadHtmlAndReturnUuid(String htmlContentEn, int startingPageNo, Long applicationId, String locale, String fileName);

	/**
	 * @param htmlContentEn
	 * @param startingPageNo
	 * @param applicationId
	 * @return
	 */
	UUID uploadHtmlAndReturnUuid(UploadHtmlDTO upload);	
}
