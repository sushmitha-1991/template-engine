package util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class PageNumberingStamper extends PdfPageEventHelper {
    private Integer totalPages = 0;


    public PageNumberingStamper(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        final int currentPageNumber = writer.getCurrentPageNumber();


        try {
            final Rectangle pageSize = document.getPageSize();

            final PdfContentByte line = writer.getDirectContent();
            line.setGrayStroke(3);
            //Footer address
            final PdfContentByte footerAddress1 = writer.getDirectContent();
            footerAddress1.setColorFill(BaseColor.GRAY);
            footerAddress1.setFontAndSize(BaseFont.createFont(), 8);
            footerAddress1.setTextMatrix(pageSize.getLeft(180), pageSize.getBottom(30));
            footerAddress1.newlineShowText("Pdf Generated");

            final PdfContentByte footerAddress2 = writer.getDirectContent();
            footerAddress2.setColorFill(BaseColor.GRAY);
            footerAddress2.setFontAndSize(BaseFont.createFont(), 8);
            footerAddress2.setTextMatrix(pageSize.getLeft(210), pageSize.getBottom(20));
            footerAddress2.newlineShowText("");


            //Page Number
            final PdfContentByte directContent = writer.getDirectContent();
            directContent.setColorFill(BaseColor.BLACK);
            directContent.setFontAndSize(BaseFont.createFont(), 10);
            directContent.setTextMatrix(pageSize.getRight(80), pageSize.getBottom(30));
            if (totalPages != 0) {
                directContent.showText(String.valueOf("Page " + currentPageNumber + " of " + totalPages));
            } else {
                directContent.showText(String.valueOf("Page | " + currentPageNumber));
            }


            //File Number
            final PdfContentByte fileNumberFooter = writer.getDirectContent();
            fileNumberFooter.setColorFill(BaseColor.BLACK);
            fileNumberFooter.setFontAndSize(BaseFont.createFont(), 10);
            fileNumberFooter.setTextMatrix(pageSize.getLeft(30), pageSize.getBottom(30));

        } catch (DocumentException | IOException e) {
            log.error("PDF generation error", e);
        }
    }
}
