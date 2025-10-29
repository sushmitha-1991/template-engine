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
	/**
	 * @param applicationInstanceId
	 * @return
	 */
	@Query(value = "SELECT oom.download_url AS docUrl, a.abbr AS abbr " + "FROM master.application_instance ai "
			+ "LEFT JOIN master.applications a ON a.id = ai.application_id "
			+ "LEFT JOIN onlyoffice.only_office_metadata oom ON oom.dsp2_key = ai.only_office_dsp2_key "
			+ "WHERE ai.id = :applicationInstanceId", nativeQuery = true)
	OnlyOfficeData getOnlyOfficeData(@Param("applicationInstanceId") Long applicationInstanceId);
	
	@Query(value = "SELECT oom.download_url AS docUrl, a.abbr AS abbr " + "FROM master.application_instance ai "
			+ "LEFT JOIN master.applications a ON a.id = ai.application_id "
			+ "LEFT JOIN onlyoffice.only_office_metadata oom ON oom.dsp2_key = ai.only_office_dsp2_key "
			+ "WHERE oom.dsp2_key = :dsp2Key", nativeQuery = true)
	OnlyOfficeData getOnlyOfficeDataByDsp2Key(@Param("dsp2Key") String dsp2Key);

	@Query(value = """
		    SELECT oom.download_url AS docUrl,
		           a.abbr AS abbr
		    FROM onlyoffice.only_office_metadata oom
		    JOIN master.application_instance ai ON ai.id = :appInstanceId
		    JOIN master.applications a ON a.id = ai.application_id
		    WHERE oom.dsp2_key = :dsp2Key
		    """, nativeQuery = true)
		OnlyOfficeData getOnlyOfficeMetaDataByDsp2Key(@Param("dsp2Key") String dsp2Key, @Param("appInstanceId") Long appInstanceId);

	@Query(value = "SELECT oom.download_url AS docUrl, a.abbr AS abbr FROM master.application_instance ai "
			+ "LEFT JOIN master.applications a ON a.id = ai.application_id "
			+ "LEFT JOIN onlyoffice.only_office_metadata oom ON oom.dsp2_key = ai.dsp2_key_hi "
			+ "WHERE ai.id = :applicationInstanceId", nativeQuery = true)
	OnlyOfficeData getOnlyOfficeDataForSynopsis(@Param("applicationInstanceId") Long applicationInstanceId);

}
