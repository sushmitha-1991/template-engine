package com.dsp.templateEngine.service;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.dsp.templateEngine.dto.ApplicationInstanceProjection;
import com.dsp.templateEngine.dto.OnlyOfficeData;
import com.dsp.templateEngine.entity.Application;
import com.dsp.templateEngine.entity.ApplicationInstance;
import com.dsp.templateEngine.entity.PublishHistoryEntity;
import com.dsp.templateEngine.repository.ApplicationInstanceRepository;
import com.dsp.templateEngine.repository.ApplicationRepository;
import com.dsp.templateEngine.repository.PublishRepository;
import com.dsp.templateEngine.repository.RegionalTranslationLogRepository;
import com.dsp.templateEngine.translation.RegionalTranslationDto;
import com.dsp.templateEngine.translation.RegionalTranslationService;
import com.dsp.templateEngine.util.Utils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RegionalManualService {

    @Autowired
    private Utils util;

    @Autowired
    private ApplicationInstanceRepository applicationInstanceRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PublishRepository publishRepository;

    @Autowired
    private RegionalTranslationLogRepository regionalTranslationLogRepository;

    @Autowired
    private RegionalTranslationService regionalTransService;

    public static final Map<String, String> CATEGORY_MAP = new HashMap<>();

    public static final Map<String, String> SUB_CATEGORY_MAP = new HashMap<>();

    public static final Map<String, String> HOUSE_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("LOB", "List of Business");
        CATEGORY_MAP.put("B1", "Bulletin Part-I");
        CATEGORY_MAP.put("B2", "Bulletin Part-II");
        CATEGORY_MAP.put("SYN", "Synopsis");
        // CATEGORY_MAP.put("PLOT", "Paper to be Laid");
    }

    static {
        SUB_CATEGORY_MAP.put("List Of Business", "Normal");
        SUB_CATEGORY_MAP.put("Revised List Of Business", "Revised List of Business");
        SUB_CATEGORY_MAP.put("Supplementary List Of Business - 1", "Supplementary List of Business-1");
        SUB_CATEGORY_MAP.put("Supplementary List Of Business - 2", "Supplementary List of Business-2");
        SUB_CATEGORY_MAP.put("Supplementary List Of Business - 3", "Supplementary List of Business-3");
    }

    static {
        HOUSE_MAP.put("RS", "rs");
        HOUSE_MAP.put("LS", "ls");
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Value("${regional.language.translation.url}")
    private String baseUrl;

    @Value("${regional.language.translation.enabled}")
    private Boolean translationFlag;

    public boolean isTranslationRequired(String abbr) {

        if (!translationFlag)
            return false;

        if (CATEGORY_MAP.get(abbr) != null) {
            return true;
        }

        return false;
    }

    private static String buildTranslateUrl(String baseUrl, Boolean edit) {
        String url = baseUrl + "/api/v1/translate-file";
        if (Boolean.TRUE.equals(edit)) {
            url += "?edit=true";
        }
        return url;
    }

    private boolean validate(RegionalTranslationDto input) {

        // Validate Mandatory Parameters
        if (StringUtils.isBlank(input.getDocumentType()) ||
                StringUtils.isBlank(input.getFileUrl()) ||
                input.getSittingDate() == null) {
            return false;
        }

        // validate application. Only specific documents are to be sent for
        if (!CATEGORY_MAP.containsKey(input.getDocumentType())) {
            return false;
        }

        // validate file exists
        URI uri = URI.create(input.getFileUrl());
        if (uri == null) {
            return false;
        }

        return true;
    }

    private void populateEndPointParams(RegionalTranslationDto input) {

        // Source file
        try {
            URL url = new URL(input.getFileUrl());
            Path temp = Files.createTempFile(UUID.randomUUID().toString(), ".docx");
            Files.copy(url.openStream(), temp, StandardCopyOption.REPLACE_EXISTING);
            input.setSource(temp.toFile());
        } catch (Exception e) {
            log.error("Failed to download file for sending to translation: {} ", e.getMessage());
        }

        // Category
        input.setCategory(CATEGORY_MAP.get(input.getDocumentType()));

        // Sub Category
        String subCategory = !StringUtils.isBlank(input.getDocumentSubType())
                ? (SUB_CATEGORY_MAP.get(input.getDocumentSubType()) != null
                        ? SUB_CATEGORY_MAP.get(input.getDocumentSubType())
                        : StringUtils.EMPTY)
                : StringUtils.EMPTY;
        input.setSubCategory(subCategory);

        // House
        input.setType(HOUSE_MAP.get("LS"));

        return;
    }

    public Map<String, Object> getDocument(String type, LocalDate forDate,
            String category, String subCategory) {
        Map<String, Object> res = new HashMap<>();

        try {
            String house = type.toUpperCase();
            System.out.println("house " + house);

            String appName = getApplicationName(category, house, subCategory);
            System.out.println("appName " + appName);
            System.out.println("fetchin publish data  date " + forDate);

            PublishHistoryEntity publishData = publishRepository
                    .fetchByDateAppName(forDate, appName, house);
            System.out.println(publishData);
            if (publishData == null) {
                res.put("data", "no data found");
                return res;
            }
            ApplicationInstance reqInstance = applicationInstanceRepository
                    .findByApplicationNoAndCurrentStatus(publishData.getApplicationNo(), "Published").get();

            OnlyOfficeData ooData = util.getODTDataIfExistsV2(reqInstance.getId());
            res.put("dsp2Key", ooData.getDsp2Key());
            res.put("sourceUrl", ooData.getDocUrl());
            res.put("appInstId", reqInstance.getId());
            return res;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return res;
    }

    private String getApplicationName(String category, String house, String subCategory) {
        try {

            System.out.println("category "+category);

            Optional<Application> appNameOpt = applicationRepository
                    .findByDocumentDisplayName(category);

            if (appNameOpt.isEmpty()) {
                appNameOpt = applicationRepository
                        .findByAbbr(category);
            }
            
            String appName=appNameOpt.get().getDsp1DocumentName();

            if (StringUtils.isNotBlank(subCategory)) {
                Map<String, String> subCategoryMap = Map.of(
                        "Revised List of Business", util.RLOB,
                        "Supplementary List Of Business", util.SLOB);

                appName = subCategoryMap.getOrDefault(subCategory.trim(), appName);
            }

            System.out.println(appName);
            return appName;

        } catch (Exception e) {
            log.error("Error while resolving application name", e);
            return category;
        }
    }

    public ResponseEntity<String> sendForRegionalLanguageTranslation(Long appInstId, String type, LocalDate forDate,
            String category,
            String subCategory, Boolean edit, String sourceUrl) {

        log.info("In Manualservice sendForRegionalLanguageTranslation");

        ApplicationInstanceProjection appDetails = util.getAppDetails(appInstId);
        RegionalTranslationDto input = new RegionalTranslationDto();
        input.setSittingDate(forDate);
        input.setDocumentType(appDetails.getAbbr());
        input.setDocumentSubType(category);
        input.setFileUrl(sourceUrl);
        input.setApplicationInstanceId(appInstId);
        input.setType(type);

        log.debug("Manual input: {} / {} / {} / {}" + input.getSittingDate(), input.getDocumentType(),
                input.getDocumentSubType(), input.getFileUrl());

        return regionalTransService.sendForTranslation(input, true);

    }

}