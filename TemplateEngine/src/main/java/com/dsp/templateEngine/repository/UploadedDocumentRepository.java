/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.UploadedDocument;

/**
 * 
 */
@Repository
public interface UploadedDocumentRepository extends JpaRepository<UploadedDocument, Long> {

}
