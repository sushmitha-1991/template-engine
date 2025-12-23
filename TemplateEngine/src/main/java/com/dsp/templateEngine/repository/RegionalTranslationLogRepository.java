package com.dsp.templateEngine.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.RegionalTranslationLog;

@Repository
public interface RegionalTranslationLogRepository extends JpaRepository<RegionalTranslationLog, Long> {
	
	boolean existsByApplicationInstanceId(Long applicationInstanceId);
	
	
	@Query(value = """		
//			select exists (select 1 from template.regional_translation_log where 
//			sitting_date = :sittingDate and
//			document_type = :documentType and 
//			document_sub_type = :documentSubType and 
//			response_status = '202 ACCEPTED') 
			
			
//		    select exists (
//		        select 1
//		        FROM template.regional_translation_log
//		        WHERE sitting_date = :sittingDate
//		          AND document_type = :documentType
//		          AND (
//		                (:documentSubType IS NULL AND document_sub_type IS NULL)
//		             OR (document_sub_type = :documentSubType)
//		              )
//		          AND response_status = '202 ACCEPTED')
			
			
		  select exists (
		        select 1 from template.regional_translation_log
		        WHERE sitting_date = :sittingDate
		          AND document_type = :documentType
		          AND document_sub_type IS NOT DISTINCT FROM :documentSubType
		          AND response_status = '202 ACCEPTED')

//			select exists (
//			    select 1 from template.regional_translation_log
//			    WHERE sitting_date = :sittingDate
//			      AND document_type = :documentType
//			      AND (
//			            CASE
//			                WHEN :documentSubType IS NULL
//			                     THEN document_sub_type IS NULL
//			                ELSE document_sub_type = :documentSubType
//			            END
//			          )
//			      AND response_status = '202 ACCEPTED')		
	
			""", nativeQuery = true)
	Boolean getTranslationEditFlag(@Param("sittingDate") LocalDate sittingDate,
								@Param("documentType") String documentType,
								@Param("documentSubType") String subDocumentType);
}
