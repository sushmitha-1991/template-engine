package model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "generate_pdf_model")
public class GeneratedPdfModel {

    private Long id;

    private Long refId;

    private String refType;

    private Integer version;

    private String html;

    private String pdfPath;
}
