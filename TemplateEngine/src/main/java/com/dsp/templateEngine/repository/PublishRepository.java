/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
