package com.dsp.templateEngine.converter;

import okhttp3.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;


@Component
@Slf4j
public class OnlyOfficePdfConverter implements IConverter {

	@Value("${onlyoffice.service.url}")
	private String onlyOfficeServiceUrl;
	
	private static final String ONLY_OFFICE_END_POINT = "/ConvertService.ashx"; 
    private static final MediaType JSON = MediaType.get("application/json");

	@Override
	public File convertToPdf (PdfConverterDTO input) throws IOException {
		
		// Extract relevant fields
		String fileName = input.getFileName();
	
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		convertToByteArray(input, pdfOutputStream);
	
		byte[] numberedPdfBytes = ConverterUtils.addPageNumbersToPdf(pdfOutputStream.toByteArray(), input.getStartPageNumber());

		// Step 3: Write PDF bytes to a temp file and return it
		File tempPdfFile = File.createTempFile(fileName, ".pdf");

		try (FileOutputStream fileOutputStream = new FileOutputStream(tempPdfFile)) {
			fileOutputStream.write(numberedPdfBytes);
		}

		return tempPdfFile;
	}


    public String extractDownloadUrl (String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

            XPath xpath = XPathFactory.newInstance().newXPath();
            return xpath.evaluate("/FileResult/FileUrl", doc);  // <-- SAFE AND CORRECT

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OnlyOffice response: " + e.getMessage(), e);
        }
    }
    
    private File downloadFile(String url, String fileName) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        File file = File.createTempFile(fileName, ".pdf");
        try (InputStream in = response.body().byteStream();
             FileOutputStream out = new FileOutputStream(file)) {

            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        }
        return file;
    }
    
    public void downloadPdfToStream(String pdfUrl, OutputStream outputStream) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(pdfUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new RuntimeException("Error downloading PDF: " + response);
            }

            try (InputStream in = response.body().byteStream()) {
                byte[] buffer = new byte[8192];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, n);
                }
            }
        }
    }

    
	public void convertToByteArray(PdfConverterDTO input, ByteArrayOutputStream outputStream) throws IOException {
		
	       // JSON body for conversion
        String json = "{\n" +
                "  \"async\": false,\n" +
                "  \"filetype\": \"docx\",\n" +
                "  \"outputtype\": \"pdf\",\n" +
                "  \"title\": \"converted\",\n" +
                "  \"url\": \"" + input.getDocUrl() + "\",\n" +
                "  \"key\": \"" + UUID.randomUUID().toString() + "\"\n" +
                "}";

        log.debug("Only Office Request Body: {}", json);
        System.out.println ("Only Office Request Body: {}" +  json);


        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(json, JSON);
        
        log.debug("Only Office URL: {}", onlyOfficeServiceUrl + ONLY_OFFICE_END_POINT);
        System.out.println("Only Office URL: {}" + onlyOfficeServiceUrl + ONLY_OFFICE_END_POINT);
        
        Request request = new Request.Builder()
                .url(onlyOfficeServiceUrl + ONLY_OFFICE_END_POINT)
                .post(body)
                .build();

        try {
	        Response response = client.newCall(request).execute();
	        if (!response.isSuccessful())
	            throw new RuntimeException("Conversion failed: " + response);
	
	        String resultJson = response.body().string(); 
	        log.debug("Result JSON: {}", resultJson);
	        System.out.println("Result JSON: {}" + resultJson);
	
	        // Extract "fileUrl" from result JSON
	        String pdfUrl = extractDownloadUrl(resultJson);
	        log.debug("URL of generated PDF: {}", pdfUrl);
	        System.out.println("URL of generated PDF: {}" + pdfUrl);
	
	        // Download the PDF
	        downloadPdfToStream(pdfUrl, outputStream);
	        
        } catch (Exception e) {
        	log.error("Failed to convert to PDF using Only Office converter: {}", e.getMessage());
        }
        
	}

}
