package com.dsp.templateEngine.controller;

import com.dsp.templateEngine.dto.enoticeboard.NewEventsRssDTO;
import com.dsp.templateEngine.dto.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.enoticeboard.NewEventsRss;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;
import com.dsp.templateEngine.service.ENoticeBoardService;
import com.dsp.templateEngine.service.NewEventsLssService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class NewEventsLssController {
    @Autowired
    private NewEventsLssService service;
    @Autowired
    private ENoticeBoardService eNoticeBoardService;
    @PostMapping("/create")
    public NewEventsLss createEvent(@RequestBody NewEventsLssDTO dto) {
        return service.saveNewEvent(dto);
    }
    @PostMapping("/create-notice")
    public NewEventsRss createNotice(@RequestBody NewEventsRssDTO dto) {
        return eNoticeBoardService.saveNewEvent(dto);
    }

}
