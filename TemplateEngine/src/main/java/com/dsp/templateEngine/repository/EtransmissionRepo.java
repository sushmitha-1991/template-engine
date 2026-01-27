package com.dsp.templateEngine.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.ETransmission;
@Repository
public interface EtransmissionRepo extends JpaRepository<ETransmission, Long> {
	@Query(value = "select op.id as paperId, o.id as officeId  from transmission.office_papers op " 
			+ " JOIN master.offices o ON op.office_id=o.id "
			+ " where op.is_active=true and op.paper_type= :paperType and o.house_id=:house", nativeQuery = true)
	Map<String,Object> getPaperId(@Param("paperType") String paperType, @Param("house") Long house);
	@Query(value = "select op.id as paperId, o.id as officeId  from transmission.office_papers op " 
			+ " JOIN master.offices o ON op.office_id=o.id "
			+ " where op.is_active=true and op.paper_type= :paperType and o.house_id=:house and o.office_code = :officeCode", nativeQuery = true)
	Map<String,Object> getPaperId(@Param("paperType") String paperType, @Param("house") Long house,
								  @Param("officeCode") String officeCode);

	
}
