package com.dsp.templateEngine.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class PublishRequestDto {
    private UUID docUuidEn;
    private UUID docUuidHi;
    private Long appInstanceId;
  
}
