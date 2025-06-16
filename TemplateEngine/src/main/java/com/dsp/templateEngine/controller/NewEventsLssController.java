package com.dsp.templateEngine.controller;

import com.dsp.templateEngine.entity.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;
import com.dsp.templateEngine.service.NewEventsLssService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class NewEventsLssController {
    @Autowired
    private NewEventsLssService service;
    @PostMapping("/create")
    public NewEventsLss createEvent(@RequestBody NewEventsLssDTO dto) {
        return service.saveNewEvent(dto);
    }
}
