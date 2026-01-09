package com.dsp.templateEngine.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.dsp.templateEngine.entity.RegionalTranslationLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.templateEngine.dto.ApplicationInstanceProjection;
import com.dsp.templateEngine.dto.OnlyOfficeData;
import com.dsp.templateEngine.dto.RegionalTranslationLogDto;
import com.dsp.templateEngine.entity.ApplicationInstance;
import com.dsp.templateEngine.entity.PublishHistoryEntity;
import com.dsp.templateEngine.repository.ApplicationInstanceRepository;
import com.dsp.templateEngine.repository.ApplicationRepository;
import com.dsp.templateEngine.repository.PublishRepository;
import com.dsp.templateEngine.response.GenericResponse;
import com.dsp.templateEngine.service.RegionalManualService;
import com.dsp.templateEngine.service.RegionalTranslationLogService;
import com.dsp.templateEngine.translation.RegionalTranslationDto;
import com.dsp.templateEngine.util.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@RestController
@RequestMapping("/template/manual")
public class RegionalTranslationManualController {

    @Autowired
    private RegionalManualService regionalManualService;

    @Autowired
    private RegionalTranslationLogService regionalTranslationLogService;

    @PostMapping(path = "/get-doc")
    public ResponseEntity<GenericResponse<Map<String,Object>>> getFile(
        @RequestParam("type") String type,
        @RequestParam("forDate") LocalDate forDate,
        @RequestParam("category") Long category,
        @RequestParam(required = false) Long subCategory)
    {
        Map<String, Object> response= regionalManualService.getDocument(type,forDate,category,subCategory);
        
         GenericResponse<Map<String, Object>> genericResponse = new GenericResponse<>(HttpStatus.OK.value(), true,
                    "Document recieved successfully.", response);

            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        
    }
    @GetMapping("/get-doc")
    public ResponseEntity<GenericResponse<Map<String,Object>>> getFileDoc(
        @RequestParam("type") String type,
        @RequestParam("forDate") LocalDate forDate,
        @RequestParam("category") Long category,
        @RequestParam(required = false) Long subCategory)
    {
        Map<String, Object> response= regionalManualService.getDocument(type,forDate,category,subCategory);
        
         GenericResponse<Map<String, Object>> genericResponse = new GenericResponse<>(HttpStatus.OK.value(), true,
                    "Document recieved successfully.", response);

            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        
    }
    @PostMapping(path = "/translate-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> translateFile(
            @RequestParam("appInstId") Long appInstId,
            @RequestParam("type") String type,
            @RequestParam("forDate") LocalDate forDate,
            @RequestParam("category") String category,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) Boolean edit,
            @RequestParam("sourceUrl") String sourceUrl) {

        try {
            ResponseEntity<String> response = regionalManualService.sendForRegionalLanguageTranslation(appInstId, type, forDate, category, subCategory, edit, sourceUrl);

            GenericResponse<String> genericResponse = new GenericResponse<>(HttpStatus.OK.value(), true,
                    "Document sent to Reverie successfully.", response.getBody());

            return new ResponseEntity<>(genericResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Failed to send Document for Regional Language translation: {}", e.getMessage());
            GenericResponse<Void> genericResponse = new GenericResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    true, "Document could not be sent to Reverie successfully.", null);

            return new ResponseEntity<>(genericResponse, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/get-translation-log")
    public ResponseEntity<GenericResponse<List<RegionalTranslationLogDto>>> getTranslationLog(
        @RequestParam(required = false) Long appInstId,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) LocalDate forDate,
        @RequestParam(required = false) LocalDate fromDate,
        @RequestParam(required = false) LocalDate toDate,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String subCategory,
        @RequestParam(required = false) Boolean edit,
        @RequestParam(required = false) String responseStatus,
        @RequestParam(required = false) Boolean manuallyTranslated) {

        try {

            List<RegionalTranslationLogDto> response =
                    regionalTranslationLogService.getTranslationLog(
                            appInstId,
                            forDate,
                            responseStatus,
                            category,
                            subCategory,
                            edit,
                            fromDate,
                            toDate,
                            type,
                            manuallyTranslated
                    );

            return ResponseEntity.ok(
                    new GenericResponse<>(
                            HttpStatus.OK.value(),
                            true,
                            "Translation logs fetched successfully",
                            response
                    )
            );

        } catch (Exception e) {
            log.error("Error while fetching Regional Translation Logs", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(
                            HttpStatus.OK.value(),
                            false,
                            "Failed to fetch translation logs",
                            null
                    ));
        }
    }

}
