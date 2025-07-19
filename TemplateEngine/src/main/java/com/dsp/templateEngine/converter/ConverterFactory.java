package com.dsp.templateEngine.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterFactory {
	
	@Autowired
	LibreHtmlConverter libreConverter;
	
	@Autowired
	LibreOdtConverter libreOdtConverter;
	
	@Autowired
	ITextConverter itextConverter;
	
	public IConverter getConverter (String applicationName, String lang) { //, String appAbbr) {
		
		System.out.println("Application Name: " + applicationName);
		System.out.println("Lang: " + lang);
		
		IConverter con = null;
		
//		if ("SW".equals(appAbbr)) {
//			return libreOdtConverter;
//		}
		
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
