package com.dsp.templateEngine.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
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
		
	@Override
	public File convertToPdf (PdfConverterDTO input) throws IOException {
		
		File tempPdfFile = File.createTempFile(input.getFileName(), ".pdf");

		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();		
		convertToByteArray(input, pdfOutputStream);		

		byte[] numberedPdfBytes = ConverterUtils.addPageNumbersToPdf(pdfOutputStream.toByteArray(), input.getStartPageNumber());

		try (FileOutputStream fileOutputStream = new FileOutputStream(tempPdfFile)) {
			fileOutputStream.write(numberedPdfBytes);
//			fileOutputStream.write(pdfOutputStream.toByteArray());  
		}
	
		return tempPdfFile;
	}
	

	@Override
	public void convertToByteArray(PdfConverterDTO input, ByteArrayOutputStream outputStream) throws IOException {

		String fontDirectory = "src/main/resources/font/";

		ConverterProperties converterProperties = new ConverterProperties();
		FontProvider fontProvider = new DefaultFontProvider();
		fontProvider.addDirectory(fontDirectory); // Load all fonts from directory
		converterProperties.setFontProvider(fontProvider);

		// Generate PDF from HTML and write it to the output stream
		PdfWriter writer = new PdfWriter(outputStream);
		PdfDocument pdfDocument = new PdfDocument(writer);
		HtmlConverter.convertToPdf(input.getHtmlContent(), pdfDocument, converterProperties);
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
	
}
