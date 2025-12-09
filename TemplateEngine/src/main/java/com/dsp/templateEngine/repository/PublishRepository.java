/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.dto.AgendaPapersDTO;
import com.dsp.templateEngine.dto.DocDetailsForSms;
import com.dsp.templateEngine.dto.MpBallotDTO;
import com.dsp.templateEngine.entity.PublishHistoryEntity;

import jakarta.transaction.Transactional;

/**
 * 
 */
@Repository
public interface PublishRepository extends JpaRepository<PublishHistoryEntity, Long> {

	@Query(value = " Select lss.lok_sabha_master_id AS lsNo, lss.id AS sessionNo,  lss.session_number AS session,"
			+ " ls.lok_sabha_number AS loksabha from master.lok_sabha_session_masters lss "
			+ " JOIN master.lok_sabha_masters ls ON ls.id = lss.lok_sabha_master_id "
			+ " where lss.id = :session and lss.lok_sabha_master_id = :loksabha ", nativeQuery = true)
	Map<String, Object> fetchLokSabhaSession(Long session, Long loksabha);

	/**
	 * @param ballotingId
	 */
	@Modifying
	@Transactional
	@Query(value = "UPDATE notices.balloting_of_notices SET is_published = true WHERE id = :ballotingId", nativeQuery = true)
	void updateIsPublish(@Param("ballotingId") Long ballotingId);

	@Query(value = """
			select pd.mp_code as mpCode,
			       nd.ballot_position as ballPos
			from notices.notice_details nd
			left join members.personal_details pd
			       on pd.id = nd.member_id
			where nd.balloting_id = :ballotingId
			""", nativeQuery = true)
	List<MpBallotDTO> getMpCodeAndBallotPosition(@Param("ballotingId") Long ballotingId);

	/**
	 * @param appInstId
	 * @return
	 */
	@Query(value = """
			select a.abbr as appAbbr,
			       ai.document_date as date,
			       a.id as appId,
			       ai.application_no as appNo
			from master.application_instance ai
			left join master.applications a
			       on a.id = ai.application_id
			where ai.id = :appInstId
			""", nativeQuery = true)
	AgendaPapersDTO getAgendaSms(@Param("appInstId") Long appInstId);

	@Query(value = """
			select ph.publish_status
			from workflow.publish_history ph
			where ph.application_no = :appNo
			limit 1
			""", nativeQuery = true)
	Boolean getPublishStatusByApplicationNo(@Param("appNo") String appNo);

	/**
	 * @param appNo
	 * @return
	 */

	@Query(value = """
			select ph.application_name as displayName,
			ph.locale as locale
			from workflow.publish_history ph
			where ph.application_no = :appNo
			""", nativeQuery = true)
	DocDetailsForSms getDisplayNameFromPublishHistory(@Param("appNo") String appNo);

}
