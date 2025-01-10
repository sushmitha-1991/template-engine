package com.dsp.templateEngine.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.dsp.templateEngine.model.GeneratedPdfModel;
import com.dsp.templateEngine.repository.GeneratedPdfModelRepository;
import com.dsp.templateEngine.service.GeneratePdfService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeneratePdfServiceImpl implements GeneratePdfService {

	@Autowired
	Environment environment;

	@Autowired
	private GeneratedPdfModelRepository generatedPdfModelRepository;

	@Value("${openKm.service.upload.endpoint}")
	private String openKmUploadEndpoint;

	@Autowired
	private RestTemplate restTemplate;

//    public static void main(String[] args) throws IOException {
//        GeneratePdfService service = new GeneratePdfService();
//        String html=null;
//        service.generatePdfFromHtml(html, "testpdf1");
//    }

	public String generatePdfFromHtml(String html, String fileName) throws IOException {
		Path finalPath = null;
		try {
			String SERVER_PATH = "C:\\\\Users\\\\LQ917SL\\\\TestPdf";
			GeneratedPdfModel pdfModel = new GeneratedPdfModel();
			Path uploadPath = Paths.get(SERVER_PATH).toAbsolutePath().normalize();

			Scanner s = new Scanner(html).useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";

//            //check record exist
//            GeneratedPdfModel temp = generatedPdfModelRepository.findTopByRefIdAndRefType(refId, refType).orElse(null);
//            if (temp != null) {
//                pdfModel.setVersion(temp.getVersion() + 1);
//            }

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			finalPath = uploadPath.resolve(fileName);

			Path temp = uploadPath.resolve("temp.pdf");

			String FONTS = "src/main/resources/font/BOOKOS.woff";
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(temp.toString()));
			ConverterProperties properties = new ConverterProperties();
			FontProvider fontProvider = new DefaultFontProvider();
			fontProvider.addDirectory(FONTS);
			properties.setFontProvider(fontProvider);

			String filePath = "C:\\Test\\temp.pdf";
			String filePathDest = "C:\\Test\\temp1.pdf";
			// dest - finalPath.toString()
			// src - temp.toString()
			// addPageNumberingAndWatermark(filePath, filePathDest, null, true, true);

			HtmlConverter.convertToPdf(result, out, properties);

			log.info("PDF path:" + SERVER_PATH + "/" + fileName);

			String fileList = "";
			// manipulatePdf(finalPath.toString());
			// addPageNumberingAndWatermark(src, dest, null, true, true);

		} catch (Exception e) {
			log.error("encountered exception", e);
		}
		return finalPath.toString();
	}

//	public static void addPageNumberingAndWatermark(String src, String dest, String base64Image, boolean showWaterMark,
//			boolean showNumbering) throws IOException, DocumentException {
//		System.out.println("src - " + src);
//		System.out.println("dest - " + dest);
//
//		int pageNumberStart = 10; // need to pass in parameters
//		PdfReader reader = new PdfReader(src);
//		int n = reader.getNumberOfPages();
//		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
//		PdfContentByte pagecontent;
//		Rectangle pagesize;
//// image watermark
//		Image img = getImageFile(base64Image);
//		float w = img.getScaledWidth();
//		float h = img.getScaledHeight();
//		float x, y;
//		for (int i = 0; i < n;) {
//			pagecontent = stamper.getOverContent(++i);
//			BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);
//			pagecontent.setFontAndSize(bf, 8);
//			pagesize = reader.getPageSizeWithRotation(i);
//			x = (pagesize.getLeft() + pagesize.getRight()) / 2;
//			y = (pagesize.getTop() + pagesize.getBottom()) / 2;
//			pagecontent = stamper.getOverContent(i);
//			if (showWaterMark) {
//				pagecontent.saveState();
//// set transparency
//				PdfGState state = new PdfGState();
//				state.setFillOpacity(0.1f);
//				pagecontent.setGState(state);
//				pagecontent.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
//				pagecontent.restoreState();
//			}
//			if (showNumbering) {
//				ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT,
//						new Phrase(String.format("Page %s", pageNumberStart)), pagesize.getRight() - 25,
//						pagesize.getBottom() + 25, 0);
//				pageNumberStart++;
//			}
//		}
//		stamper.close();
//		reader.close();
//
//	}

//	public String generatePdfFromHtml(String html, String fileName) throws IOException {
//		Path finalPath = null;
//		try {
//			String SERVER_PATH = "C:\\Users\\LQ917SL\\TestPdf";
//			GeneratedPdfModel pdfModel = new GeneratedPdfModel();
//			Path uploadPath = Paths.get(SERVER_PATH).toAbsolutePath().normalize();
//
//			// Check if the HTML content is empty
//			if (html == null || html.trim().isEmpty()) {
//				throw new IllegalArgumentException("HTML content cannot be empty");
//			}
//
//			// Create directories if they don't exist
//			if (!Files.exists(uploadPath)) {
//				Files.createDirectories(uploadPath);
//			}
//
//			finalPath = uploadPath.resolve(fileName); // Ensure this is used for saving the final file
//
//			// Ensure no file is open before attempting to write
//			File fileToWrite = finalPath.toFile();
//			if (fileToWrite.exists() && fileToWrite.isFile() && !fileToWrite.delete()) {
//				throw new IOException("Failed to delete existing file before writing");
//			}
//
//			// Create the actual PDF using the correct finalPath
//			try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalPath.toString()))) {
//				ConverterProperties properties = new ConverterProperties();
//				FontProvider fontProvider = new DefaultFontProvider();
//				fontProvider.addDirectory("src/main/resources/font/BOOKOS.woff");
//				properties.setFontProvider(fontProvider);
//
//				// Convert HTML to PDF
//				HtmlConverter.convertToPdf(html, out, properties);
//			} catch (Exception e) {
//				log.error("Error during HTML to PDF conversion", e);
//				throw new IOException("Failed to generate PDF from HTML", e);
//			}
//
//			// After PDF is generated, add watermark and page numbering
//			String filePathDest = finalPath.toString(); // Use the finalPath to add watermark and numbering
//			addPageNumberingAndWatermark(finalPath.toString(), filePathDest, null, true, true);
//
//			log.info("PDF generated successfully at: " + finalPath.toString());
//		} catch (Exception e) {
//			log.error("Error generating PDF", e);
//			throw new IOException("Error generating PDF", e);
//		}
//		return finalPath.toString();
//	}
//
//	public static void addPageNumberingAndWatermark(String src, String dest, String base64Image, boolean showWaterMark,
//			boolean showNumbering) throws IOException, DocumentException {
//		int pageNumberStart = 10; // Starting page number
//		PdfReader reader = null;
//		PdfStamper stamper = null;
//
//		try {
//			reader = new PdfReader(src);
//			int n = reader.getNumberOfPages();
//			stamper = new PdfStamper(reader, new FileOutputStream(dest));
//
//			PdfContentByte pagecontent;
//			Rectangle pagesize;
//			// image watermark
//			Image img = getImageFile(base64Image); // This method should load image from base64
//			float w = img.getScaledWidth();
//			float h = img.getScaledHeight();
//			float x, y;
//
//			for (int i = 1; i <= n; i++) { // Start from page 1 to n (not 0)
//				pagecontent = stamper.getOverContent(i);
//				BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);
//				pagecontent.setFontAndSize(bf, 8);
//				pagesize = reader.getPageSizeWithRotation(i);
//				x = (pagesize.getLeft() + pagesize.getRight()) / 2;
//				y = (pagesize.getTop() + pagesize.getBottom()) / 2;
//
//				if (showWaterMark) {
//					pagecontent.saveState();
//					PdfGState state = new PdfGState();
//					state.setFillOpacity(0.1f); // Set watermark transparency
//					pagecontent.setGState(state);
//					pagecontent.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2)); // Add watermark
//					pagecontent.restoreState();
//				}
//
//				if (showNumbering) {
//					ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT,
//							new Phrase(String.format("Page %s", pageNumberStart)), pagesize.getRight() - 25,
//							pagesize.getBottom() + 25, 0);
//					pageNumberStart++;
//				}
//			}
//		} finally {
//			if (stamper != null) {
//				stamper.close(); // Manually close the stamper
//			}
//			if (reader != null) {
//				reader.close(); // Manually close the reader
//			}
//		}
//	}

	/***
	 * <p>
	 * Create Image from base64Image String.
	 * </p>
	 *
	 * @param base64Image
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Image getImageFile(String base64Image) {
		base64Image = !StringUtils.isEmpty(base64Image) ? base64Image
				: "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";
		Image img = null;
		try {
			img = Image.getInstance(Base64.decode(base64Image));
		} catch (BadElementException e) {
			log.info("Error message:{}", e.getMessage());
		} catch (IOException e) {
			log.info("Error message:{}", e.getMessage());
		}
		return img;
	}

//	public static void manipulatePdf(String pdfSourceURL) throws Exception {
//		String dest = pdfSourceURL;
////        log.info(WATERMARK_LOGO_PATH);
////        String imageURL =WATERMARK_LOGO_PATH;
//		Integer totalPages = 0;
//
//		Document document = new Document();
//		File file = new File(pdfSourceURL);
//		FileInputStream fileInputStream = new FileInputStream(file);
//		PdfReader reader = new PdfReader(fileInputStream);
//		totalPages = totalPages + reader.getNumberOfPages();
//
//		try {
//			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(dest)));
//
//			// Page number
//			writer.setPageEvent(new PageNumberingStamper(totalPages));
//
//			document.open();
//			document.setMargins(72, 72, 72, 72);
//			PdfContentByte pageContentByte = writer.getDirectContent();
//			PdfImportedPage pdfImportedPage;
//			int currentPdfReaderPage = 1;
//			int pages_total = 0;
//			pages_total += reader.getNumberOfPages();
//
//			while (currentPdfReaderPage <= reader.getNumberOfPages()) {
//				document.newPage();
//				pdfImportedPage = writer.getImportedPage(reader, currentPdfReaderPage);
//				pageContentByte.addTemplate(pdfImportedPage, 0, 0);
////                pageContentByte.setTextMatrix(100,100);
//				currentPdfReaderPage++;
//			}
//			document.close();
////			WatermarkStamper.addWaterMark(pdfSourceURL);
//			log.info("Numbering and Water Mark added in PDF Successfully!!");
//		} catch (DocumentException e) {
//			log.error("Numbering not added in PDF Document Exception!!");
//			throw new RuntimeException(e);
//		} catch (FileNotFoundException e) {
//			log.error("Numbering not added in PDF File Not Found Exception!!");
//			throw new RuntimeException(e);
//		}
//	}

	@Transactional
	@Override
	public ResponseEntity<Map<String, Object>> publishDocument(String html, String fileName, Long appInstId,
			Integer startPageNumber) {
		try {

			File pdfFile = convertHtmlToPdf(html, fileName, startPageNumber);

			String uploadResult = uploadPdfToOpenKM(pdfFile, fileName, "/okm:root/", "231", appInstId.toString(),
					"ApplicationInstance", "1");

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(uploadResult, new TypeReference<>() {
			});

			log.debug("Response Map from OpenKM: {}", responseMap);

			if (responseMap == null || !responseMap.containsKey("data")) {
				throw new IllegalStateException("Invalid response: 'data' key is missing.");
			}

			Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
			if (data == null || !data.containsKey("documentUuid")) {
				throw new IllegalStateException("Invalid response: 'documentUuid' is missing in 'data'.");
			}

			UUID documentUuid = UUID.fromString(data.get("documentUuid").toString());
			System.out.println("### documentUuid - " + documentUuid);
			log.debug("Extracted documentUuid: {}", documentUuid);

			generatedPdfModelRepository.updateApplicationInstanceStatusToPublished(appInstId, documentUuid);

			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error publishing document", e);

			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("statusCode", 500);
			errorResponse.put("success", false);
			errorResponse.put("message", "Failed to publish document. Error: " + e.getMessage());
			errorResponse.put("data", null);

			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public File generatePdfFromHtmlOpenKm(String html, String fileName) throws IOException {
		try {
			// Define the directory where the PDF will be stored
			String SERVER_PATH = "C:\\Users\\LQ917SL\\TestPdf";
			Path uploadPath = Paths.get(SERVER_PATH).toAbsolutePath().normalize();

			// Ensure the upload directory exists
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Define the final path for the PDF file
			Path finalPath = uploadPath.resolve(fileName);

			// Convert HTML to a PDF
			String FONTS = "src/main/resources/font/BOOKOS.woff";
			ConverterProperties properties = new ConverterProperties();
			FontProvider fontProvider = new DefaultFontProvider();
			fontProvider.addDirectory(FONTS);
			properties.setFontProvider(fontProvider);

			try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalPath.toString()))) {
				Scanner s = new Scanner(html).useDelimiter("\\A");
				String result = s.hasNext() ? s.next() : "";
				HtmlConverter.convertToPdf(result, out, properties);
			}

			log.info("PDF generated at path: " + finalPath.toString());

			// Return the generated File
			return finalPath.toFile();
		} catch (Exception e) {
			log.error("Error generating PDF", e);
			throw new RuntimeException("Failed to generate PDF", e);
		}
	}

	public String uploadPdfToOpenKM(File pdfFile, String documentName, String folderPath, String masterId,
			String referenceId, String referenceType, String applicationNo) {
		try {
			// The JWT token is automatically added by the RestTemplate interceptor
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			// Create a FileSystemResource from the file
			FileSystemResource fileResource = new FileSystemResource(pdfFile);

			// Prepare the body with all required fields
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("folderPath", folderPath);
			body.add("documentName", documentName);
			body.add("file", fileResource);
			body.add("masterId", masterId);
			body.add("referenceId", referenceId);
			body.add("referenceType", referenceType);
			body.add("applicationNo", applicationNo);

			// Prepare the request entity with headers and body
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			// String openKmUrl = "https://dsptest.sansad.in/dms/openkm/document/upload";

			ResponseEntity<String> response = restTemplate.exchange(openKmUploadEndpoint, HttpMethod.POST,
					requestEntity, String.class);

			// Return the response body as a string
			return response.getBody();
		} catch (HttpClientErrorException e) {
			log.error("Error in HTTP request to OpenKM: ", e);
			throw new RuntimeException("Error uploading document to OpenKM. HTTP Error: " + e.getMessage());
		} catch (Exception e) {
			log.error("Error uploading PDF to OpenKM", e);
			throw new RuntimeException("Error uploading document to OpenKM.", e);
		}
	}

	public File convertHtmlToPdf(String htmlContent, String fileName, Integer startPageNumber) throws IOException {
		// Create a temporary file for the final PDF
		// To adapt your convertHtmlToPdf function to return a File object without
		// writing it to the local file system, you can use a TempFile approach. Java
		// provides a mechanism to create temporary files that are automatically deleted
		// when the program exits, which is ideal for your use case.
		File tempPdfFile = File.createTempFile(fileName, ".pdf");

		// Generate the initial PDF in memory
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		HtmlConverter.convertToPdf(htmlContent, pdfOutputStream);

		// Add page numbering to the in-memory PDF
		byte[] numberedPdfBytes = addPageNumbersToPdf(pdfOutputStream.toByteArray(), startPageNumber);

		// Write the numbered PDF bytes to the temporary file
		try (FileOutputStream fileOutputStream = new FileOutputStream(tempPdfFile)) {
			fileOutputStream.write(numberedPdfBytes);
		}

		// Return the temporary file
		return tempPdfFile;
	}

	private byte[] addPageNumbersToPdf(byte[] pdfBytes, Integer startPageNumber) throws IOException {
		int startingPage = (startPageNumber != null) ? startPageNumber : 1;

		// Input and output streams for in-memory processing
		ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfBytes);
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

		// Open the source PDF from the input stream
		PdfReader reader = new PdfReader(pdfInputStream);
		PdfWriter writer = new PdfWriter(pdfOutputStream);
		PdfDocument pdfDoc = new PdfDocument(reader, writer);

		// Create a Document object
		Document document = new Document(pdfDoc);
		int totalPages = pdfDoc.getNumberOfPages();

		// Add page numbers at the center of each page's bottom margin
		for (int i = 1; i <= totalPages; i++) {
			document.showTextAligned(new Paragraph(String.valueOf(startingPage + i - 1)) // Page number with offset
					.setFontSize(12) // Set font size for better visibility
					.setBold(), // Make it bold if needed
					297.5f, // X position (center for A4 width: 595 / 2)
					15, // Y position (bottom margin)
					i, // Page number
					TextAlignment.CENTER, // Align text to the center horizontally
					VerticalAlignment.BOTTOM, // Align text to the bottom vertically
					0 // Rotation angle (0 for no rotation)
			);
		}

		// Close the document
		document.close();

		// Return the final PDF as a byte array
		return pdfOutputStream.toByteArray();
	}

}
