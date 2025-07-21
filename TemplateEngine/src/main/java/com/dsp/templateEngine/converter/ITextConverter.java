package com.dsp.templateEngine.converter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

@Component
public class ITextConverter implements IConverter{
	
	
	public File convertToPdf (PdfConverterDTO input) throws IOException {
		
		// Extract relevant fields
		String htmlContent = input.getHtmlContent();
		String fileName = input.getFileName();
		Integer startPageNumber = input.getStartPageNumber();
		
		return convertHtmlToPdf(htmlContent, fileName, startPageNumber); 
	}
	

	public File convertHtmlToPdf(String htmlContent, String fileName, Integer startPageNumber) throws IOException {
		
		File tempPdfFile = File.createTempFile(fileName, ".pdf");

		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		
		convertHtmlToPdfByteArray(htmlContent, pdfOutputStream);

		byte[] numberedPdfBytes = addPageNumbersToPdf(pdfOutputStream.toByteArray(), startPageNumber);

		try (FileOutputStream fileOutputStream = new FileOutputStream(tempPdfFile)) {
			fileOutputStream.write(numberedPdfBytes);
		}

		return tempPdfFile;
	}

	public void convertHtmlToPdfByteArray(String htmlContent, 
			java.io.ByteArrayOutputStream outputStream)  {

		String fontDirectory = "src/main/resources/font/";

		ConverterProperties converterProperties = new ConverterProperties();
		FontProvider fontProvider = new DefaultFontProvider();
		fontProvider.addDirectory(fontDirectory); // Load all fonts from directory
		converterProperties.setFontProvider(fontProvider);

		// Generate PDF from HTML and write it to the output stream
		PdfWriter writer = new PdfWriter(outputStream);
		PdfDocument pdfDocument = new PdfDocument(writer);
		HtmlConverter.convertToPdf(htmlContent, pdfDocument, converterProperties);
		pdfDocument.close();

	}

	
	private byte[] addPageNumbersToPdf(byte[] pdfBytes, Integer startPageNumber) throws IOException {
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
	
	@Override
	public File convertOdtToPdf(String docUrl, String fileName) {
		throw new UnsupportedOperationException("Not supported for ITextConverter");
	}

}
