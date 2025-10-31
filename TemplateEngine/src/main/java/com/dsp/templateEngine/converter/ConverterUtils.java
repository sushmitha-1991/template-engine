package com.dsp.templateEngine.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

public class ConverterUtils {
	
	
	public static byte[] addPageNumbersToPdf(byte[] pdfBytes, Integer startPageNumber) throws IOException {
		int startingPage = (startPageNumber != null) ? startPageNumber : 1;

		ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfBytes);
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

		PdfReader reader = new PdfReader(pdfInputStream);
		PdfWriter writer = new PdfWriter(pdfOutputStream);
		PdfDocument pdfDoc = new PdfDocument(reader, writer);
		Document document = new Document(pdfDoc);
		int totalPages = pdfDoc.getNumberOfPages();

		// Load Tahoma font from resources or file system
		// Replace this path with actual location if needed
		PdfFont tahomaFont = PdfFontFactory.createFont("src/main/resources/font/tahoma.ttf");

		for (int i = 1; i <= totalPages; i++) {
			document.showTextAligned(
					new Paragraph(String.valueOf(startingPage + i - 1)).setFontSize(10).setFont(tahomaFont), 297.5f, // X
					15, // Y position (bottom)
					i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
		}

		document.close();
		return pdfOutputStream.toByteArray();
	}

}
