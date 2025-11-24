/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.PublishHistoryEntity;

/**
 * 
 */
@Repository
public interface PublishRepository extends JpaRepository<PublishHistoryEntity, Long> {
    @Query(value = "select op.id from transmission.office_papers op "
            + " JION master.offices o ON op.office_id=o.id "
            + " where op.paper_type ilike :paperType and o.house_id=:house", nativeQuery = true)
    List<Long> getPaperId(@Param("paperType") String paperType,
            @Param("house") Long house);
}
