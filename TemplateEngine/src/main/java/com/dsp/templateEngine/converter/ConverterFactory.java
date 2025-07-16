package com.dsp.templateEngine.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterFactory {
	
	@Autowired
	LibreConverter libreConverter;
	
	@Autowired
	ITextConverter itextConverter;
	
	public IConverter getConverter (String applicationName, String lang) {
		
		System.out.println("Application Name: " + applicationName);
		System.out.println("Lang: " + lang);
		
		IConverter con = null;
		
		if ("hi".equalsIgnoreCase(lang) || ("_hi").equals(lang)) {
			return libreConverter;
		} 
		
		return itextConverter;
		
//		switch (applicationName) {
//			
//			case "SN":
//			case "PN":
//			case "ES": 
//				con = "_hi".equalsIgnoreCase(lang) ? libreConverter : itextConverter;
//				break;
//			default:
//				con = itextConverter;
//				break;
//		}		
//
//		System.out.println("Selected converter: " + con);
//		return con;

	}

}
