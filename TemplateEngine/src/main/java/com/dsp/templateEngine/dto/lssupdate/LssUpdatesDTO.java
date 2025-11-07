package com.dsp.templateEngine.dto.lssupdate;
    
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LssUpdatesDTO{

    private Integer eId;
    private String title;
    private String file;
    private String url;
    private LocalDate date;
    private String type;
    private Integer isNew;
    private Integer isActive;
    private String username;
    private Integer userId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime publishedAt;
    private String publishedBy;
    private String ipAddr;
    private String locale;
    private String uuid;
}
