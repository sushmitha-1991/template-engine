/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

/**
 * 
 */
@Component
public class AddPageNumberToPdfFile {

	public byte[] addPageNumbersToPdf(byte[] pdfBytes, Integer startPageNumber) throws IOException {
		int startingPage = (startPageNumber != null) ? startPageNumber : 1;

		// Input and output streams for in-memory processing
		ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfBytes);
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

		// Open the source PDF from the input stream
		PdfReader reader = new PdfReader(pdfInputStream);
		PdfWriter writer = new PdfWriter(pdfOutputStream);
		PdfDocument pdfDoc = new PdfDocument(reader, writer);

		// Create a Document object
		Document document = new Document(pdfDoc);
		int totalPages = pdfDoc.getNumberOfPages();

		// Add page numbers at the center of each page's bottom margin
		for (int i = 1; i <= totalPages; i++) {
			document.showTextAligned(new Paragraph(String.valueOf(startingPage + i - 1)) // Page number with offset
					.setFontSize(12) // Set font size for better visibility
					.setBold(), // Make it bold if needed
					297.5f, // X position (center for A4 width: 595 / 2)
					15, // Y position (bottom margin)
					i, // Page number
					TextAlignment.CENTER, // Align text to the center horizontally
					VerticalAlignment.BOTTOM, // Align text to the bottom vertically
					0 // Rotation angle (0 for no rotation)
			);
		}

		// Close the document
		document.close();

		// Return the final PDF as a byte array
		return pdfOutputStream.toByteArray();
	}

}
