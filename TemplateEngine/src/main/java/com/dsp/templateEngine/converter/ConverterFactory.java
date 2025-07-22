package com.dsp.templateEngine.converter;

import org.apache.commons.lang3.StringUtils;
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
		
		if (StringUtils.isEmpty(applicationName)) { 
			return itextConverter;
		}	
		
		// get House Id
		Long houseId = Utils.getHouseId();
		if (houseId == 1) {
			switch (applicationName) {
			
				case "SN":
					return libreConverter;
				case "PN":
					return libreConverter;
				default:
					return itextConverter;
			}		
		}
		
		return itextConverter;

	}

}
