package com.dsp.templateEngine.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.ETransmission;

@Repository
public interface EtransmissionRepo extends JpaRepository<ETransmission, Long> {
 @Query(value = "select op.id from transmission.office_papers op "
            + " JOIN master.offices o ON op.office_id=o.id "
            + " where op.paper_type ilike :paperType and o.house_id=:house", nativeQuery = true)
    List<Long> getPaperId(@Param("paperType") String paperType,
            @Param("house") Long house);
}

