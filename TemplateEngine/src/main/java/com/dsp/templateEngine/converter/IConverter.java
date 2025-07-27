package com.dsp.templateEngine.converter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public interface IConverter {

	File convertToPdf (PdfConverterDTO input) throws IOException;
	
	void convertToByteArray(PdfConverterDTO input, ByteArrayOutputStream outputStream) throws IOException;

//	File convertHtmlToPdf (String htmlContent, String fileName, Integer startPageNumber) throws IOException;
//	
//	File convertOdtToPdf(String docUrl, String fileName) throws IOException;
//	
//	void convertHtmlToPdfByteArray(String htmlContent, ByteArrayOutputStream outputStream);
	
}
