package com.dsp.templateEngine.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderCreateReqDTO {

    private String parentPath;
    private String lsNo;
    private String sesNo;
    private String houseAbbr; 
    private String rsNo;
    private String appAbbr;
    
}