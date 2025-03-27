/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.PublishHistoryEntity;

/**
 * 
 */
@Repository
public interface PublishRepository extends JpaRepository<PublishHistoryEntity, Long> {

}
