/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.dto.OnlyOfficeData;
import com.dsp.templateEngine.entity.OnlyOfficeMetadata;

/**
 * 
 */
@Repository
public interface OnlyOfficeMetadataRepository extends JpaRepository<OnlyOfficeMetadata, Long> {

	/**
	 * @param documentKey
	 * @return
	 */
	Optional<OnlyOfficeMetadata> findByDocumentKey(String documentKey);

	/**
	 * @param key
	 * @return
	 */
	Optional<OnlyOfficeMetadata> findByDsp2Key(String key);

	/**
	 * @param applicationInstanceId
	 * @return
	 */
	@Query(value = "SELECT oom.download_url AS docUrl, a.abbr AS abbr " + "FROM master.application_instance ai "
			+ "LEFT JOIN master.applications a ON a.id = ai.application_id "
			+ "LEFT JOIN master.only_office_metadata oom ON oom.dsp2_key = ai.only_office_dsp2_key "
			+ "WHERE ai.id = :applicationInstanceId", nativeQuery = true)
	OnlyOfficeData getOnlyOfficeData(@Param("applicationInstanceId") Long applicationInstanceId);

}
