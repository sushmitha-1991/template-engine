package dsp.templateEngine.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dsp.templateEngine.response.GenericResponse;
import dsp.templateEngine.service.GeneratePdfService;

@RestController
@RequestMapping("/template")
public class GeneratedPdfController {

	@Autowired
	private GeneratePdfService pdfService;

	@PostMapping("/generatePdfFromHtml")
	public ResponseEntity<GenericResponse<String>> generatePdfFromHtml(@RequestParam String html,
			@RequestParam String fileName) {
		try {
			String pdfPath = pdfService.generatePdfFromHtml(html, fileName);
			GenericResponse<String> response = new GenericResponse<>(HttpStatus.OK.value(), true,
					"PDF generated successfully.", pdfPath);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (IOException e) {
			GenericResponse<String> response = new GenericResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
					"PDF generation failed.", null);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
