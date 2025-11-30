/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.PublishHistoryEntity;

/**
 * 
 */
@Repository
public interface PublishRepository extends JpaRepository<PublishHistoryEntity, Long> {

    @Query(value = " Select lss.session_number from master.lok_sabha_session_masters lss "+
        " JOIN master.lok_sabha_masters ls ON ls.id = lss.lok_sabha_master_id "+
        " where lss.id = :session and ls.lok_sabha_number = :loksabha ",nativeQuery = true)
        
    String fetchLokSabhaSession(String session,String loksabha);
   
}
