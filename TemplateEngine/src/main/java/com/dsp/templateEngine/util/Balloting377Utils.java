package com.dsp.templateEngine.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dsp.templateEngine.dto.PublishHistoryDTO;
import com.dsp.templateEngine.entity.ETransmission;
import com.dsp.templateEngine.entity.PublishHistoryEntity;
import com.dsp.templateEngine.repository.EtransmissionRepo;

@Component
public class Balloting377Utils {

	public static final String NOTICE_377 = "377";
	public static final String NOTICE_ZERO_HOUR = "ZH";
	public static final String BALLOT_RESULT = "Ballot Result (Matters under rule 377)";

	@Autowired
	private EtransmissionRepo etransmissionRepo;

	public void saveIntoEtransmission(PublishHistoryEntity publishEntity, String house, String title,
			PublishHistoryDTO dto) {

		Long officePaperId = getPaperId(BALLOT_RESULT);
		System.out.println(">>> officePaperId " + officePaperId);
		ETransmission transmission = ETransmission.builder().lokSabhaNo(Long.valueOf(dto.getLokSabhaNo()))
				.lokSabhaSessionNo(Long.valueOf(dto.getLokSabhaSessionNo()))
				.rajyaSabhaSessionNo(Long.valueOf(dto.getRajyaSabhaSessionNo())).officepaperid(officePaperId)
				.fileEnglish(publishEntity.getPublishUuid()).dateOfCirculation(publishEntity.getForDate()).build();
		etransmissionRepo.save(transmission);
	}

	public Long getPaperId(String paperType) {
		System.out.println(">>> paperType - " + paperType);
		List<Long> paperIds = etransmissionRepo.getPaperId(paperType, Utils.getHouseId());
		return paperIds.isEmpty() ? null : paperIds.get(0);
	}
}