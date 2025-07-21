package com.dsp.templateEngine.converter;


import java.io.ByteArrayOutputStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfConverterDTO {

    private String htmlContent; // For HTML --> PDF
    private String fileName;
    private Integer startPageNumber;
    
    private ByteArrayOutputStream outputStream; // For ODT --> PDF & HTML --> PDF
    
    private String docUrl; // For ODT --> PDF
    
}