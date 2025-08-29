package com.dsp.templateEngine.dto;

import lombok.Data;

@Data
public class DownloadRequestDTO {
    String htmlContentEn;
    String htmlContentHi;
    String fileName;
    Long applicationInstanceId;
}
