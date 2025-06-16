package com.dsp.templateEngine.entity.nelss;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "new_events_lss", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventsLss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "e_id")
    private Long id;

    @Column(name = "e_title")
    private String etitle;

    @Column(name = "h_title")
    private String htitle;

    @Column(name = "e_file")
    private String efile;

    @Column(name = "h_file")
    private String hfile;

    @Column(name = "e_url")
    private String eurl;

    @Column(name = "h_url")
    private String hurl;

    @Column(name = "e_date")
    private LocalDate edate;

    @Column(name = "e_type")
    private String etype;

    @Column(name = "h_type")
    private String htype;

    @Column(name = "isnew")
    private Integer isNew;

    @Column(name = "isactive")
    private Integer isActive;

}
