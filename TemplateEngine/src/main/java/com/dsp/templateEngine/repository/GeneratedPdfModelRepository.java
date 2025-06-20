package com.dsp.templateEngine.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsp.templateEngine.dto.ApplicationInstanceProjection;
import com.dsp.templateEngine.entity.GeneratedPdfModel;

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
	@Query(value = "UPDATE master.application_instance SET current_status = 'Published', is_published = true, published_document_uuid_en = :documentUuid, published_document_uuid_hi = :documentUuidHi  WHERE id = :appInstId", nativeQuery = true)
	void updateApplicationInstanceStatusToPublishedEn(Long appInstId, UUID documentUuid, UUID documentUuidHi);

	@Transactional
	@Modifying
	@Query(value = "UPDATE master.application_instance SET current_status = 'Published', is_published = true, published_document_uuid_hi = :documentUuid WHERE id = :appInstId", nativeQuery = true)
	void updateApplicationInstanceStatusToPublishedHi(Long appInstId, UUID documentUuid);

	@Query(value = """
			    SELECT ai.application_no AS appNo,
			           a.abbr AS abbr,
					   a.dsp1_document_name AS appName
			    FROM master.application_instance ai
			    LEFT JOIN master.applications a ON a.id = ai.application_id
			    WHERE ai.id = :appInstId
			""", nativeQuery = true)
	ApplicationInstanceProjection findApplicationDetailsById(@Param("appInstId") Long appInstId);

	/**
	 * @param appInstId
	 * @return
	 */
	@Query(value = "select ai.is_published FROM master.application_instance ai WHERE ai.id = :appInstId", nativeQuery = true)
	Boolean isApplicationInstancePublished(@Param("appInstId") Long appInstId);

	/**
	 * @param appInstId
	 */

	@Transactional
	@Modifying
	@Query(value = "UPDATE workflow.workflow_history SET status = 'Published' WHERE id = :workflowHistoryId", nativeQuery = true)
	void updateStatusInWorkflowHistory(Long workflowHistoryId);

	/**
	 * @param appInstId
	 * @return
	 */
	@Query(value = " select MAX(wh.id) from workflow.workflow_history wh where wh.application_instance_id = :appInstId", nativeQuery = true)
	Long getWorkflowIdByAppInstanceId(Long appInstId);

	@Query(value = " select ur.office_id from authentication.user_roles ur where user_id =:userId limit 1", nativeQuery = true)
	Long getOfficeIdUsingUserId(@Param("userId") Long userId);

	/**
	 * @param applicationId
	 * @return
	 */
	@Query(value = " select a.abbr from master.applications a where a.id =:applicationId ", nativeQuery = true)
	String findAbbrByApplicationId(Long applicationId);

	/**
	 * @param applicationId
	 * @return
	 */
	@Query(value = " select a.dsp1_document_name from master.applications a where a.id =:applicationId ", nativeQuery = true)
	String findDSPAppNameByApplicationId(Long applicationId);

}
