package dsp.templateEngine.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import dsp.templateEngine.model.GeneratedPdfModel;
import dsp.templateEngine.repository.GeneratedPdfModelRepository;
import dsp.templateEngine.service.GeneratePdfService;
import dsp.templateEngine.util.PageNumberingStamper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeneratePdfServiceImpl implements GeneratePdfService {

    @Autowired
    Environment environment;
    @Autowired
    private GeneratedPdfModelRepository generatedPdfModelRepository;

//    public static void main(String[] args) throws IOException {
//        GeneratePdfService service = new GeneratePdfService();
//        String html=null;
//        service.generatePdfFromHtml(html, "testpdf1");
//    }


    public String generatePdfFromHtml(String html, String fileName) throws IOException {
        Path finalPath = null;
        try {
            String SERVER_PATH = "C:\\\\Users\\\\LQ917SL\\\\OneDrive - EY\\\\Documents\\\\TestPdf";
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
            String FONTS = "src/main/resources/font/BOOKOS.woff";
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalPath.toString()));
            ConverterProperties properties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider();
            fontProvider.addDirectory(FONTS);
            properties.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(result, out, properties);
            log.info("PDF path:" + SERVER_PATH + "/" + fileName);

            manipulatePdf(finalPath.toString());

        } catch (Exception e) {
            log.error("encountered exception", e);
        }
        return finalPath.toString();
    }



    public static void manipulatePdf(String pdfSourceURL) throws Exception {
        String dest = pdfSourceURL;
//        log.info(WATERMARK_LOGO_PATH);
//        String imageURL =WATERMARK_LOGO_PATH;
        Integer totalPages = 0;

        Document document = new Document();
        File file = new File(pdfSourceURL);
        FileInputStream fileInputStream = new FileInputStream(file);
        PdfReader reader = new PdfReader(fileInputStream);
        totalPages = totalPages + reader.getNumberOfPages();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(dest)));
            writer.setPageEvent(new PageNumberingStamper(totalPages));
            document.open();
            document.setMargins(72, 72, 72, 72);
            PdfContentByte pageContentByte = writer.getDirectContent();
            PdfImportedPage pdfImportedPage;
            int currentPdfReaderPage = 1;
            int pages_total = 0;
            pages_total += reader.getNumberOfPages();
            while (currentPdfReaderPage <= reader.getNumberOfPages()) {
                document.newPage();
                pdfImportedPage = writer.getImportedPage(reader, currentPdfReaderPage);
                pageContentByte.addTemplate(pdfImportedPage, 0, 0);
//                pageContentByte.setTextMatrix(100,100);
                currentPdfReaderPage++;
            }
            document.close();
//            WatermarkStamper.addWaterMark(pdfSourceURL);
            log.info("Numbering and Water Mark added in PDF Successfully!!");
        } catch (DocumentException e) {
            log.error("Numbering not added in PDF Document Exception!!");
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            log.error("Numbering not added in PDF File Not Found Exception!!");
            throw new RuntimeException(e);
        }
    }


}
