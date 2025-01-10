package com.dsp.templateEngine.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.dsp.templateEngine.model.GeneratedPdfModel;

import jakarta.transaction.Transactional;

public interface GeneratedPdfModelRepository extends JpaRepository<GeneratedPdfModel, Long> {

	@Query("select pdf from GeneratedPdfModel pdf where refId=:refId and refType=:refType order by id desc")
	Optional<GeneratedPdfModel> findTopByRefIdAndRefType(Long refId, String refType);

	/**
	 * @param appInstId
	 * @param documentUuid
	 */

	@Transactional
	@Modifying
	@Query(value = "UPDATE master.application_instance SET current_status = 'Published', is_published = true, published_document_uuid = :documentUuid WHERE id = :appInstId", nativeQuery = true)
	void updateApplicationInstanceStatusToPublished(Long appInstId, UUID documentUuid);
}
