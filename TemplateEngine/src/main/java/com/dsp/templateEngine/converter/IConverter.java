package com.dsp.templateEngine.converter;

import java.io.File;
import java.io.IOException;

public interface IConverter {

	File convertHtmlToPdf (String htmlContent, String fileName, Integer startPageNumber) throws IOException;

}
