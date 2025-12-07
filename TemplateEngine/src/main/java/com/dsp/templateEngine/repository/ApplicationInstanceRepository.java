package com.dsp.templateEngine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsp.templateEngine.dto.UploadedDocumentProjection;
import com.dsp.templateEngine.entity.ApplicationInstance;

public interface ApplicationInstanceRepository extends JpaRepository<ApplicationInstance, Long> {

	@Query(value = "select c.abbr from agenda_papers.lob l " + "left join master.codes c on c.id =l.lob_type "
			+ "where l.id=:refId and c.is_active=true ", nativeQuery = true)
	Optional<String> fetchLobDetailsByRefId(Long refId);

	/**
	 * @param appInstId
	 * @return
	 */
	@Query(value = """
			select translation_document_type_id from master.application_instance where id = :appInstId
			""", nativeQuery = true)
	Long getTranslationFlag(@Param("appInstId") Long appInstId);

	/**
	 * @param translAppId
	 * @return
	 */
	@Query(value = """
			select dsp1_document_name from master.applications where id = :translAppId
			""", nativeQuery = true)
	String getApplicationName(Long translAppId);

	@Query(value = """
			select a.abbr from master.application_instance ai left join master.applications a on a.id = ai.application_id where ai.id = :translAppId
			""", nativeQuery = true)
	String getApplicationNameByInstId(Long translAppId);

	/**
	 * @param string
	 * @return
	 */
	@Query(value = """
			select id from master.houses where abbr = :house""", nativeQuery = true)
	Long getHouseIdByHouseAbbr(String house);

	/**
	 * @param appInstId
	 * @return
	 */
	@Query(value = """
			select
			    ai.application_id as appId,
			    lsm.id as lsNo,
			    lssm.id as lsSesNo,
			    pd.mp_code as mpCode,
			    ai.published_document_uuid_en as documentUuid,
			    nd.notice_date as noticeDate
			from master.application_instance ai
			left join notices.notice_details nd on nd.id = ai.ref_id
			left join members.personal_details pd on pd.id = nd.member_id
			left join master.lok_sabha_masters lsm on lsm.lok_sabha_number = ai.lok_sabha_session
			left join master.lok_sabha_session_masters lssm on lssm.session_number = ai.lok_sabha_session_number
			where ai.id = :appInstId
			""", nativeQuery = true)
	UploadedDocumentProjection getAppId(Long appInstId);
	
}
