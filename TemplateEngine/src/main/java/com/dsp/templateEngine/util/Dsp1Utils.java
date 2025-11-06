
package com.dsp.templateEngine.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dsp.templateEngine.dto.ApplicationInstanceProjection;
import com.dsp.templateEngine.dto.PublishHistoryDTO;
import com.dsp.templateEngine.dto.enoticeboard.NewEventsRssDTO;
import com.dsp.templateEngine.dto.lssupdate.LssUpdatesDTO;
import com.dsp.templateEngine.dto.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.ApplicationInstance;
import com.dsp.templateEngine.entity.BaseUser;
import com.dsp.templateEngine.entity.PublishHistoryEntity;
import com.dsp.templateEngine.entity.enoticeboard.NewEventsRss;
import com.dsp.templateEngine.entity.lssupdate.LssUpdates;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;
import com.dsp.templateEngine.repository.ApplicationInstanceRepository;
import com.dsp.templateEngine.repository.MyUserRepository;
import com.dsp.templateEngine.service.ENoticeBoardService;
import com.dsp.templateEngine.service.NewEventsLssService;
import com.ibm.icu.text.RuleBasedNumberFormat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 */
@Component
public class Dsp1Utils {

	@Autowired
	private Utils util;
	@Autowired
	private MyUserRepository userRepository;
	@Autowired
	private EntityManager entityManager; // For native query execution

	@Autowired
	private NewEventsLssService nelssService;

	@Autowired
	private ENoticeBoardService eNoticeBoardService;

	private final String LOK_SABHA = "LS";

	private final String RAJYA_SABHA = "RS";

	private static final String SUMMON = "SN";

	private static final String PROROGATION = "PN";

	private static final String LOB = "LIST_OF_BUSINESS";

	private static final String RLOB = "REVISED_LIST_OF_BUSINESS";

	private static final String SLOB = "SUPPLEMENTARY_LIST_OF_BUSINESS";

	private static final String BALLOTING_OF_NOTICES = "BON";

	public void writeToBallotingDSP1(PublishHistoryEntity publishHistoryEntity, String houseAbbr, String ballotTitle) {

		try {
			Long sessionId = Long.parseLong(publishHistoryEntity.getLokSabhaSessionNo());
			Long lokSabhaId = Long.parseLong(publishHistoryEntity.getLokSabhaNo());

			Integer sessionNo = (Integer) entityManager.createNativeQuery(
					"SELECT session_number_int FROM master.lok_sabha_session_masters WHERE id = :id")
					.setParameter("id", sessionId)
					.getSingleResult();

			String lokSabhaNumberStr = (String) entityManager.createNativeQuery(
					"SELECT lok_sabha_number FROM master.lok_sabha_masters WHERE id = :id")
					.setParameter("id", lokSabhaId)
					.getSingleResult();

			int lokSabhaNo = Integer.parseInt(lokSabhaNumberStr);
			RuleBasedNumberFormat ordinalFmt = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.ORDINAL);
			String ordinal = ordinalFmt.format(sessionNo);
			String ordinalLs = ordinalFmt.format(lokSabhaNo);

			// 5. Convert to word form (optional)
			String ordNoLs = Utils.ordinalToWord(ordinalLs);

			// 6. Final title
			String eTitle = "Balloting of Notices " + ordinal + " Session of " + ordNoLs + " Lok Sabha.";

			NewEventsLssDTO nELss = NewEventsLssDTO.builder().isActive(1).edate(publishHistoryEntity.getForDate())
					.eurl(publishHistoryEntity.getPublishUrl())
					.efile(fetchUuid(publishHistoryEntity.getPublishUrl()))
					.etitle(eTitle)
					.isNew(1)
					.etype("dsp2En")
					.build();
			NewEventsLss lss = nelssService.saveNewEvent(nELss);
			System.out.println("Function call which table " + lss);
		} catch (NoResultException e) {
			throw new RuntimeException("Session or Lok Sabha number not found for given IDs.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in writeToBallotingDSP1", e);
		}
	}

	public void writeToDSP1(ApplicationInstanceProjection appDetails,
			PublishHistoryDTO publishHistoryDto, PublishHistoryDTO hindiDTO, String houseAbbr) {
		System.out.println("house " + houseAbbr);
		if (houseAbbr.equalsIgnoreCase("LS")) {
			NewEventsLssDTO nELss = NewEventsLssDTO.builder().isActive(1).edate(publishHistoryDto.getForDate())
					.eurl(publishHistoryDto.getPublishUrl())
					.hurl(hindiDTO.getPublishUrl())
					.hfile(fetchUuid(hindiDTO.getPublishUrl()))
					.efile(fetchUuid(publishHistoryDto.getPublishUrl()))
					.etitle(getTitle(appDetails, publishHistoryDto, houseAbbr))
					.htitle(getTitleHi(appDetails, hindiDTO, houseAbbr))
					.isNew(1)
					.etype("dsp2En")
					.htype("dsp2Hi")
					.build();
			NewEventsLss lss = nelssService.saveNewEvent(nELss);
			System.out.println(lss);
		} else if (houseAbbr.equalsIgnoreCase("RS")) {

			NewEventsRssDTO nERss = NewEventsRssDTO.builder()
					.startDate(publishHistoryDto.getForDate().atStartOfDay())
					.endDate(publishHistoryDto.getForDate().atTime(LocalTime.MAX))
					.fileName(fetchUuid(publishHistoryDto.getPublishUrl()))
					.title(getTitle(appDetails, publishHistoryDto, houseAbbr))
					.language("English")
					.pdfFileUrl(publishHistoryDto.getPublishUrl())
					.build();
			NewEventsRssDTO nHRss = NewEventsRssDTO.builder()
					.startDate(hindiDTO.getForDate().atStartOfDay())
					.fileName(fetchUuid(hindiDTO.getPublishUrl()))
					.endDate(hindiDTO.getForDate().atTime(LocalTime.MAX))
					.title(getTitleHi(appDetails, hindiDTO, houseAbbr))
					.language("Hindi")
					.pdfFileUrl(hindiDTO.getPublishUrl())
					.build();

			NewEventsRss rss = eNoticeBoardService.saveNewEvent(nERss);
			if (hindiDTO != null) {
				NewEventsRss rssHi = eNoticeBoardService.saveNewEvent(nHRss);
				System.out.println(rssHi);
			}
			System.out.println(rss);
		}
	}

	public void writeToDSP1Lss(ApplicationInstanceProjection appDetails,
			PublishHistoryDTO publishHistoryDto, PublishHistoryDTO hindiDTO, String houseAbbr) {
		System.out.println("house " + houseAbbr);
		BaseUser user = getUser();
		// if (houseAbbr.equalsIgnoreCase("LS")) {
		LssUpdatesDTO lssUpdateEn = LssUpdatesDTO.builder()
				.isActive(1)
				.date(publishHistoryDto.getForDate())
				.file(fetchUuid(publishHistoryDto.getPublishUrl()))
				.title(getTitle(appDetails, publishHistoryDto, houseAbbr))
				.isNew(1)
				.type("dsp2En")
				.uuid(publishHistoryDto.getPublishUuid().toString())
				.locale("en")
				.createdAt(LocalDateTime.now())
				.publishedAt(LocalDateTime.now())
				.createdBy(user.getUsername())
				.publishedBy(user.getUsername())
				.build();
		LssUpdates lssEn = nelssService.saveLssUpdates(lssUpdateEn);
		LssUpdatesDTO lssUpdateHi = LssUpdatesDTO.builder()
				.isActive(1)
				.date(hindiDTO.getForDate())
				.file(fetchUuid(hindiDTO.getPublishUrl()))
				.title(getTitleHi(appDetails, hindiDTO, houseAbbr))
				.isNew(1)
				.type("dsp2Hi")
				.uuid(hindiDTO.getPublishUuid().toString())
				.locale("hi")
				.createdAt(LocalDateTime.now())
				.publishedAt(LocalDateTime.now())
				.createdBy(user.getUsername())
				.publishedBy(user.getUsername())
				.build();
		LssUpdates lssHi = nelssService.saveLssUpdates(lssUpdateHi);
		System.out.println(lssEn);

		System.out.println(lssHi);
		// } else if (houseAbbr.equalsIgnoreCase("RS")) {

		// NewEventsRssDTO nERss = NewEventsRssDTO.builder()
		// .startDate(publishHistoryDto.getForDate().atStartOfDay())
		// .endDate(publishHistoryDto.getForDate().atTime(LocalTime.MAX))
		// .fileName(fetchUuid(publishHistoryDto.getPublishUrl()))
		// .title(getTitle(appDetails, publishHistoryDto, houseAbbr))
		// .language("English")
		// .pdfFileUrl(publishHistoryDto.getPublishUrl())
		// .build();
		// NewEventsRssDTO nHRss = NewEventsRssDTO.builder()
		// .startDate(hindiDTO.getForDate().atStartOfDay())
		// .fileName(fetchUuid(hindiDTO.getPublishUrl()))
		// .endDate(hindiDTO.getForDate().atTime(LocalTime.MAX))
		// .title(getTitleHi(appDetails, hindiDTO, houseAbbr))
		// .language("Hindi")
		// .pdfFileUrl(hindiDTO.getPublishUrl())
		// .build();

		// NewEventsRss rss = eNoticeBoardService.saveNewEvent(nERss);
		// if (hindiDTO != null) {
		// NewEventsRss rssHi = eNoticeBoardService.saveNewEvent(nHRss);
		// System.out.println(rssHi);
		// }
		// System.out.println(rss);
		// }
	}

	private BaseUser getUser() {
		// TODO Auto-generated method stub
		Long user = util.getUserIdFromAuthentication();
		Optional<BaseUser> userDetails = userRepository.findById(user);
		return userDetails.isPresent() ? userDetails.get() : null;
	}

	private String fetchUuid(String url) {
		String[] parts = url.split("/");
		String fileName = parts[parts.length - 1];
		return fileName;
	}

	private String getTitleHi(ApplicationInstanceProjection appDetails, PublishHistoryDTO publishHistoryDto,
			String houseAbbr) {
		String eTitle = "";
		switch (houseAbbr) {
			case LOK_SABHA:
				eTitle = getTitleHiLS(appDetails, publishHistoryDto);
				break;
			case RAJYA_SABHA:
				eTitle = getTitleHiRS(appDetails, publishHistoryDto);
				break;
		}
		return eTitle;
	}

	private String getTitleHiLS(ApplicationInstanceProjection appDetails, PublishHistoryDTO publishHistoryDto) {
		Integer sesNo = Utils.romanToInt(publishHistoryDto.getLokSabhaSessionNo());
		RuleBasedNumberFormat ordinalFmt = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.ORDINAL);
		String ordinalLs = ordinalFmt.format(Integer.parseInt(publishHistoryDto.getLokSabhaNo()));
		String ordinal = ordinalFmt.format(sesNo);
		String ordNoLs = Utils.ordinalToWord(ordinalLs);
		String eTitle = "";
		switch (appDetails.getAbbr()) {
			case SUMMON:
				eTitle += publishHistoryDto.getLokSabhaNo() + "वीं लोक सभा के "
						+ getHindiOrdinal(publishHistoryDto.getLokSabhaSessionNo()) + " सत्र का आह्वान|";
				break;
			case PROROGATION:
				eTitle += publishHistoryDto.getLokSabhaNo() + "वीं लोक सभा के "
						+ getHindiOrdinal(publishHistoryDto.getLokSabhaSessionNo()) + " सत्र की अनवरोधन";
				break;
			case BALLOTING_OF_NOTICES:
				eTitle += "Balloting of Notices  " + ordinal + " Session of " + ordNoLs + " Lok Sabha.";
				break;
		}
		return eTitle;
	}

	private String getTitleHiRS(ApplicationInstanceProjection appDetails, PublishHistoryDTO publishHistoryDto) {

		String eTitle = "";

		switch (appDetails.getAbbr()) {

			case SUMMON:
				eTitle += "राज्य सभा के " + publishHistoryDto.getRajyaSabhaSessionNo() + "वें सत्र का आह्वान|";
				break;

			case PROROGATION:
				eTitle += "राज्य सभा के " + publishHistoryDto.getRajyaSabhaSessionNo() + "वें सत्र की अनवरोधन";
				break;
		}

		return eTitle;
	}

	public static String getHindiOrdinal(String input) {
		Map<Integer, String> hindiMap = Map.ofEntries(
				Map.entry(1, "पहले"),
				Map.entry(2, "दूसरे"),
				Map.entry(3, "तीसरे"),
				Map.entry(4, "चौथे"),
				Map.entry(5, "पांचवे"),
				Map.entry(6, "छठे"),
				Map.entry(7, "सातवें"),
				Map.entry(8, "आठवें"),
				Map.entry(9, "नौवें"),
				Map.entry(10, "दसवें"),
				Map.entry(11, "ग्यारहवें"),
				Map.entry(12, "बारहवें"),
				Map.entry(13, "तेरहवें"),
				Map.entry(14, "चौदहवें"),
				Map.entry(15, "पंद्रहवें"),
				Map.entry(16, "सोलहवें"),
				Map.entry(17, "सत्रहवें"),
				Map.entry(18, "अठारहवें"),
				Map.entry(19, "उन्नीसवें"),
				Map.entry(20, "बीसवें"));

		Map<String, Integer> romanMap = Map.ofEntries(
				Map.entry("I", 1), Map.entry("II", 2), Map.entry("III", 3), Map.entry("IV", 4),
				Map.entry("V", 5), Map.entry("VI", 6), Map.entry("VII", 7), Map.entry("VIII", 8),
				Map.entry("IX", 9), Map.entry("X", 10), Map.entry("XI", 11), Map.entry("XII", 12),
				Map.entry("XIII", 13), Map.entry("XIV", 14), Map.entry("XV", 15), Map.entry("XVI", 16),
				Map.entry("XVII", 17), Map.entry("XVIII", 18), Map.entry("XIX", 19), Map.entry("XX", 20));

		int number;
		try {
			number = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			number = romanMap.getOrDefault(input.toUpperCase(), -1);
		}

		return hindiMap.getOrDefault(number, "अमान्य संख्या");
	}

	private String getTitle(ApplicationInstanceProjection appDetails, PublishHistoryDTO publishHistoryDto,
			String houseAbbr) {

		String eTitle = "";

		switch (houseAbbr) {
			case LOK_SABHA:
				eTitle = getTitleLS(appDetails, publishHistoryDto);
				break;
			case RAJYA_SABHA:
				eTitle = getTitleRS(appDetails, publishHistoryDto);
				break;
		}

		return eTitle;
	}

	private String getTitleLS(ApplicationInstanceProjection appDetails, PublishHistoryDTO publishHistoryDto) {

		Integer sesNo = Utils.romanToInt(publishHistoryDto.getLokSabhaSessionNo());

		RuleBasedNumberFormat ordinalFmt = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.ORDINAL);
		String ordinalLs = ordinalFmt.format(Integer.parseInt(publishHistoryDto.getLokSabhaNo()));

		String ordinal = ordinalFmt.format(sesNo);

		String ordNo = Utils.ordinalToWord(ordinal);
		String ordNoLs = Utils.ordinalToWord(ordinalLs);

		String eTitle = "";

		switch (appDetails.getAbbr()) {

			case SUMMON:
				eTitle += "Summoning of " + ordinal + " Session of " + ordNoLs + " Lok Sabha.";
				break;
			case PROROGATION:
				eTitle += "Prorogation of " + ordNo + " Session of " + ordNoLs + " Lok Sabha.";
				break;
			case BALLOTING_OF_NOTICES:
				eTitle += "Balloting of Notices  " + ordinal + " Session of " + ordNoLs + " Lok Sabha.";
				break;

		}

		return eTitle;
	}

	private String getTitleRS(ApplicationInstanceProjection appDetails, PublishHistoryDTO publishHistoryDto) {

		RuleBasedNumberFormat ordinalFmt = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.ORDINAL);

		String ordinalRs = ordinalFmt.format(Integer.parseInt(publishHistoryDto.getRajyaSabhaSessionNo()));

		String ordNoRs = Utils.ordinalToWord(ordinalRs);

		String eTitle = "";

		switch (appDetails.getAbbr()) {

			case SUMMON:
				eTitle += "Summoning of " + ordinalRs + " Session of Rajya Sabha.";
				break;

			case PROROGATION:
				eTitle += "Prorogation of " + ordNoRs + " Session of Rajya Sabha.";
				break;
		}

		return eTitle;
	}

	// get ListOf Business abbr to Insert into publish history
	public String getLobApplicationName(PublishHistoryDTO publishHistoryDTO,
			ApplicationInstance instance, ApplicationInstanceRepository applicationInstanceRepository) {
		Optional<String> lobType = applicationInstanceRepository.fetchLobDetailsByRefId(instance.getRefId());
		if (lobType.isPresent())
			return responseAgainstLobType(lobType.get());
		return null;
	}

	private String responseAgainstLobType(String lobType) {

		if (compare(lobType, "LOB_BUSINESS_TYPE_RS_LOB") ||
				compare(lobType, "LOB_BUSINESS_TYPE_LS_LOB")) {
			return LOB;
		} else if (compare(lobType, "LOB_BUSINESS_TYPE_RS_Revised_LOB") ||
				compare(lobType, "LOB_BUSINESS_TYPE_LS_Revised LOB")) {
			return RLOB;
		} else if (compare(lobType, "LOB_BUSINESS_TYPE_RS_Supplementary_LOB") ||
				compare(lobType, "LOB_BUSINESS_TYPE_LS_Supplementary LOB")) {
			return SLOB;
		}
		return null;
	}

	private Boolean compare(String v1, String v2) {
		return v1.equalsIgnoreCase(v2);
	}

}
