package com.dsp.templateEngine.entity.enoticeboard;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "new_events_lss", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventsRss {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Title")
    private String title;

    @Column(name = "venue")
    private String venue;
    // venue
    @Column(name = "Section")
    private String section;

    @Column(name = "FileNo")
    private String fileNo;

    @Column(name = "Type")
    private String type;

    @Column(name = "PdfFileUrl")
    private String pdfFileUrl;

    @Column(name = "ImageFileUrl")
    private String imageFileUrl;

    @Column(name = "StartDate")
    private LocalDateTime startDate;
    @Column(name = "EndDate")
    private LocalDateTime endDate;
    @Column(name = "FileName")
    private String fileName;
    @Column(name = "AddImageOrNot")
    private String addImageOrNot;

    @Column(name = "Language")
    private String language;


}
