package com.dsp.templateEngine.converter;

import okhttp3.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.io.*;

@Service
@Slf4j
public class OnlyOfficePdfConverter implements IConverter {

	@Value("${onlyoffice.service.url}")
	private String onlyOfficeServiceUrl;
	
	private static final String ONLY_OFFICE_END_POINT = "/ConvertService.ashx"; 
    private static final MediaType JSON = MediaType.get("application/json");

    public File convertToPdf (PdfConverterDTO input) {

        // JSON body for conversion
        String json = "{\n" +
                "  \"async\": false,\n" +
                "  \"filetype\": \"docx\",\n" +
                "  \"outputtype\": \"pdf\",\n" +
                "  \"title\": \"converted\",\n" +
                "  \"url\": \"" + input.getDocUrl() + "\"\n" +
                "}";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(onlyOfficeServiceUrl + ONLY_OFFICE_END_POINT)
                .post(body)
                .build();

        try {
	        Response response = client.newCall(request).execute();
	        if (!response.isSuccessful())
	            throw new RuntimeException("Conversion failed: " + response);
	
	        String resultJson = response.body().string();
	
	        // Extract "fileUrl" from result JSON
	        String pdfUrl = extractDownloadUrl(resultJson);
	
	        // Download the PDF
	        return downloadFile(pdfUrl, input.getFileName());
        } catch (Exception e) {
        	log.error("Failed to convert to PDF using Only Office converter: {}", e.getMessage());
        	return null;
        }
    }

    private String extractDownloadUrl(String json) {
        // Expected response: {"endConvert":true,"fileUrl":"http://...."}
        int start = json.indexOf("\"fileUrl\":\"") + 11;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
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
    
	public void convertToByteArray(PdfConverterDTO input, ByteArrayOutputStream outputStream) throws IOException {
		// do nothing
	}

}
