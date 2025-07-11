package com.dsp.templateEngine.entity.enoticeboard;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventsRssDTO {
        private Long id;

    private String title;

    private String venue;
    // venue
    private String section;

    private String fileNo;

    private String type;

    private String pdfFileUrl;

    private String imageFileUrl;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String fileName;
    private String addImageOrNot;

    private String language;
    
}
