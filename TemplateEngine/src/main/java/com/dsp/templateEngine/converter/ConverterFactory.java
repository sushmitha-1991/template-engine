package com.dsp.templateEngine.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dsp.templateEngine.util.Utils;

@Component
public class ConverterFactory {
	
	@Autowired
	LibreHtmlConverter libreConverter;
	
	@Autowired
	LibreOdtConverter libreOdtConverter;
	
	@Autowired
	ITextConverter itextConverter;
	
	public IConverter getConverter (String applicationName, String lang) {
		
		System.out.println("Application Name: " + applicationName);
		System.out.println("Lang: " + lang);
		
		if ("SW".equals(applicationName)) {
			return libreOdtConverter;
		}

		if ("hi".equalsIgnoreCase(lang) || ("_hi").equals(lang)) {
			return libreConverter;
		} 
		
		// get House Id
		Long houseId = Utils.getHouseId();
		if (houseId == 2) {
			switch (applicationName) {
			
				case "SN":
				case "PN":
					return libreConverter;
				default:
					return itextConverter;
			}		
		}
		
		return itextConverter;

	}

}
