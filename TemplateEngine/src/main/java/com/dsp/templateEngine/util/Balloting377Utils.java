package com.dsp.templateEngine.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dsp.templateEngine.dto.PublishHistoryDTO;
import com.dsp.templateEngine.entity.ETransmission;
import com.dsp.templateEngine.entity.PublishHistoryEntity;
import com.dsp.templateEngine.repository.EtransmissionRepo;

import ch.qos.logback.classic.pattern.Util;

@Component
public class Balloting377Utils {

	public static final String NOTICE_377 = "377";
	public static final String NOTICE_ZERO_HOUR = "ZH";
	public static final String BALLOT_RESULT = "Ballot Result (Matters under rule 377)";
	public static final String TABLE_OFF = "LS_TOA";
	@Autowired
	private EtransmissionRepo etransmissionRepo;
	@Autowired
	private Utils utils;

	public void saveIntoEtransmission(PublishHistoryEntity publishEntity, String house, String title,
			PublishHistoryDTO dto) {

		System.out.println("LS No: " + dto.getLokSabhaNo());
		System.out.println("LS Session: " + dto.getLokSabhaSessionNo());
		System.out.println("RS Session: " + dto.getRajyaSabhaSessionNo());

		Map<String, Object> paperData = getOfficePaperId(BALLOT_RESULT);
		Long officePaperId = paperData.get("paperId") != null
				? ((Number) paperData.get("paperId")).longValue()
				: null;
		Long officeId = paperData.get("officeId") != null
				? ((Number) paperData.get("officeId")).longValue()
				: null;
		System.out.println(">>> officePaperId " + officePaperId);
		ETransmission transmission =
		ETransmission.builder().lokSabhaNo(Long.valueOf(dto.getLokSabhaNo()))
		.lokSabhaSessionNo(Long.valueOf(dto.getLokSabhaSessionNo())).rajyaSabhaSessionNo(null)
		.officepaperid(officePaperId).fileEnglish(publishEntity.getPublishUuid())
		.dateOfCirculation(publishEntity.getForDate()).officeId(officeId).build();
		
		etransmissionRepo.save(transmission);
	}

	public Map<String, Object> getOfficePaperId(String paperType) {

		System.out.println(">>> paperType - " + paperType);
		Map<String, Object> paperIds = etransmissionRepo.getPaperId(paperType, Utils.getHouseId(), TABLE_OFF);
		return paperIds.isEmpty() ? null : paperIds;
	}

}