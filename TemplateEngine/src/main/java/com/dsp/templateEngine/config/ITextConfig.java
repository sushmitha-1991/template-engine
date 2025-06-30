package com.dsp.templateEngine.config;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

//import com.itextpdf.licensing.base.LicenseKey;

import jakarta.annotation.PostConstruct;

@Component
public class ITextConfig {
	
    @PostConstruct
    public void init() throws IOException {
//    	LicenseKey.loadLicenseFile(new File("resources/license/itextkey.json"));
    	
//    	ClassPathResource resource = new ClassPathResource("license/itextkey.json");
//        LicenseKey.loadLicenseFile(resource.getInputStream());    	
    	
    }
}
    