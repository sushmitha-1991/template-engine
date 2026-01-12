package com.dsp.templateEngine.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegionalTranslationLogDto {

    private String responseBody;
    private String responseStatus;
    private LocalDate sittingDate;
    private String documentType;
    private String documentSubType;
    private Boolean editFlag;
}
