package com.dsp.templateEngine.entity.nelss;
import java.time.LocalDate;

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
public class NewEventsLssDTO {
    private String efile;
    private String htitle;
    private String etitle;
    private String hfile;
    private String eurl;
    private String hurl;
    private LocalDate edate;
    private String etype;
    private String htype;
    private Integer isNew;
    private Integer isActive;
    
}
