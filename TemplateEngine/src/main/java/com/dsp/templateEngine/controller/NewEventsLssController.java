package com.dsp.templateEngine.controller;

import com.dsp.templateEngine.dto.ApplicationInstanceProjection;
import com.dsp.templateEngine.dto.FolderCreateDTO;
import com.dsp.templateEngine.dto.FolderCreateReqDTO;
import com.dsp.templateEngine.dto.PublishRequestDto;
import com.dsp.templateEngine.dto.enoticeboard.NewEventsRssDTO;
import com.dsp.templateEngine.dto.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.enoticeboard.NewEventsRss;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;
import com.dsp.templateEngine.service.ENoticeBoardService;
import com.dsp.templateEngine.service.FolderStructureService;
import com.dsp.templateEngine.service.GeneratePdfService;
import com.dsp.templateEngine.service.NewEventsLssService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/template")
public class NewEventsLssController {
    @Autowired
    private NewEventsLssService service;
    @Autowired
    private ENoticeBoardService eNoticeBoardService;
    @Autowired
    private FolderStructureService folderStructureService;
    @Autowired 
    private GeneratePdfService generatePdfService;
    @PostMapping("/create")
    public NewEventsLss createEvent(@RequestBody NewEventsLssDTO dto) {
        return service.saveNewEvent(dto);
    }
    @PostMapping("/create-notice")
    public NewEventsRss createNotice(@RequestBody NewEventsRssDTO dto) {
        return eNoticeBoardService.saveNewEvent(dto);
    }

    @PostMapping("/create-folder-structure")
    public String  createFolderStructure(@RequestBody FolderCreateReqDTO dto) {
        List<String> folders=new ArrayList<>();
        if(StringUtils.isBlank(dto.getParentPath()))
        {
            dto.setParentPath("/okm:root");
        }
        folders.add(dto.getHouseAbbr());
        if(dto.getHouseAbbr().equalsIgnoreCase("LS"))
        {
            folders.add(dto.getLsNo());
            folders.add(dto.getSesNo());
        }
        else{
            folders.add(dto.getRsNo());
        }
        folders.add(dto.getAppAbbr());
        
        System.out.println("folders "+folders);
        return folderStructureService.createDmsFolderStructure( folders);
    }
    @PostMapping("/entry-in-publish-history")
    public String publishHistoryEntry(@RequestBody PublishRequestDto  dto) {
       return  generatePdfService.savedSuccess(dto.getAppInstanceId(), dto.getDocUuidEn(), dto.getDocUuidHi());
    }

}
