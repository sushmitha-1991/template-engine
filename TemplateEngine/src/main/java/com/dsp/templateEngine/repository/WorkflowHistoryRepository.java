/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.WorkflowHistory;

/**
 * Repository interface for WorkflowHistory entity.
 * 
 * Provides CRUD operations and any custom query methods.
 */
@Repository
public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, Long> {

}
