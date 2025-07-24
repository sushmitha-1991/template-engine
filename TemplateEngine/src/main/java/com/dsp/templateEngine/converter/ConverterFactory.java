package com.dsp.templateEngine.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dsp.templateEngine.util.Utils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConverterFactory {
	
	@Autowired
	Utils utils;
	
	@Autowired
	LibreHtmlConverter libreConverter;
	
	@Autowired
	LibreOdtConverter libreOdtConverter;
	
	@Autowired
	ITextConverter itextConverter;
	
	public IConverter getConverter (String applicationName, String lang) {
		
		log.debug("Application Name: " + applicationName);
		log.debug("Lang: " + lang);
		
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
		String houseAbbr = utils.getHouseAbbr();
		log.debug("House Abbr: " + houseAbbr);

		
		if (Utils.RAJYA_SABHA.equals(houseAbbr)) {
			
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
