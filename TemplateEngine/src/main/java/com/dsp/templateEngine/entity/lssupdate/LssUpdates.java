package com.dsp.templateEngine.entity.lssupdate;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lss_updates", schema = "ls-cms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LssUpdates{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "e_id")
    private Integer eId;

    @Column(name = "title")
    private String title;

    @Column(name = "file")
    private String file;

    @Column(name = "url")
    private String url;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "type")
    private String type;

    @Column(name = "is_new")
    private Integer isNew;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "username")
    private String username;

    @Column(name = "userid")
    private Integer userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "published_by")
    private String publishedBy;

    @Column(name = "ip_addr")
    private String ipAddr;

    @Column(name = "locale")
    private String locale;

    @Column(name = "uuid")
    private String uuid;
}
