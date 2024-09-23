package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.GeneratePdfService;

import java.io.IOException;

@RestController
@RequestMapping("/generate")
public class GeneratedPdfController {

    @Autowired
    private GeneratePdfService pdfService;

    @PostMapping("/generatePdfFromHtml")
    public String generatePdfFromHtml(@RequestParam String html,@RequestParam String fileName) throws IOException{
            return pdfService.generatePdfFromHtml(html, fileName);
    }
}
