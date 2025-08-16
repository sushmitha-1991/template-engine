package com.dsp.templateEngine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
