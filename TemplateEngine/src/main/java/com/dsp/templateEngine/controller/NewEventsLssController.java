package com.dsp.templateEngine.controller;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.templateEngine.dto.FolderCreateReqDTO;
import com.dsp.templateEngine.dto.PublishRequestDto;
import com.dsp.templateEngine.dto.enoticeboard.NewEventsRssDTO;
import com.dsp.templateEngine.dto.lssupdate.LssUpdatesDTO;
import com.dsp.templateEngine.dto.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.enoticeboard.NewEventsRss;
import com.dsp.templateEngine.entity.lssupdate.LssUpdates;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;
import com.dsp.templateEngine.service.ENoticeBoardService;
import com.dsp.templateEngine.service.FolderStructureService;
import com.dsp.templateEngine.service.GeneratePdfService;
import com.dsp.templateEngine.service.NewEventsLssService;
import com.dsp.templateEngine.service.TranslationService;

@RestController
@RequestMapping("/template")
public class NewEventsLssController {
    @Autowired
    private NewEventsLssService service;
    @Autowired
    private TranslationService translationService;
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
     @PostMapping("/create/lss-updates")
    public LssUpdates createUpdate(@RequestBody LssUpdatesDTO dto) {
        return service.saveLssUpdates(dto);
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

    
@PostMapping("/callTranslation")
    public String uploadTranslationFile(
            @RequestParam String applicationName,
            @RequestParam String type,
            @RequestParam String subCategory,
            @RequestParam LocalDate date,
            @RequestParam boolean edit,
            @RequestParam("filePath") String filePath
    ) {
        File file = new File(filePath);
        return translationService.callTranslateFileAPI(applicationName, type, subCategory, date, edit, file);
    }
    @GetMapping("/download/{uuid}")
    public File downloadAPI(
            @PathVariable UUID uuid
    ) {
        return translationService.getFileFromUUID(uuid);
    }

}
