package com.dsp.templateEngine.converter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public interface IConverter {

	File convertHtmlToPdfFile (String htmlContent, String fileName, Integer startPageNumber) throws IOException;
	
	public void convertHtmlToPdfByteArray(String htmlContent, ByteArrayOutputStream outputStream);
}
