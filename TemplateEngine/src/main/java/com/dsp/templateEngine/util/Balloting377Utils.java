package com.dsp.templateEngine.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dsp.templateEngine.entity.PublishHistoryEntity;
import com.dsp.templateEngine.repository.PublishRepository;

public class Balloting377Utils {
    public static final String NOTICE_377="377";
    public static final String NOTICE_ZERO_HOUR="ZH";
    @Autowired
    private PublishRepository publishRepo;
    @Autowired
    private Utils utils;

    public void saveIntoEtransmission(PublishHistoryEntity publishEntity,String house,
        String title,PublishHistoryDTO dto)
    {
        Long officePaperId=getPaperId(NOTICE_377);
        ETransmission transmission=Etransmission.builder()
        .lokSabhaNo(dto.getLokSabhaNo())
        .lokSabhaSessionNo(dto.getLokSabhaSessionNo())
        .rajyaSabhaSessionNo(dto.getRajyaSabhaSessionNo())
        .officePaperId(officePaperId)
        .fileEnglish(publishEntity.getPublishUuid())
        .dateOfCirculation()
        .build();
    }
    public Long getPaperId(String paperType)
    {
        List<Long> paperId=publishRepo.getPaperId("%"+paperType+"%",utils.getHouseId());
        if(paperId.size()>0)
        {
            return paperId.get(0);
        }
        return null;
    }
}
