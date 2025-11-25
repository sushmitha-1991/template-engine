package com.dsp.templateEngine.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.ETransmission;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EtransmissionRepo extends JpaRepository<ETransmission, Long> {
	@Query(value = "select op.id as paperId, o.id as officeId  from transmission.office_papers op " 
			+ " JOIN master.offices o ON op.office_id=o.id "
			+ " where op.paper_type= :paperType and o.house_id=:house", nativeQuery = true)
	Map<String,Object> getPaperId(@Param("paperType") String paperType, @Param("house") Long house);

	
    // void saveEntityQuery(String lokSabhaNo, String lokSabhaSessionNo, Long officeId, Long officePaperId,
    //         UUID publishUuid, LocalDate forDate, Long userIdFromAuthentication, LocalDateTime now);
	@Transactional
	@Modifying
@Query(value =
    "INSERT INTO transmission.e_transmission (" +
    "id, lok_sabha_no, lok_sabha_session_no, office_id, office_paper_id, " +
    "date_of_circulation, date_of_sitting, " +
    "file_english, file_english_ver, file_hindi, file_hindi_ver, " +
    "file_english_update, file_hindi_update, " +
    "errata_file_english, errata_file_hindi, " +
    "content, category_id, " +
    "created_by, created_at, updated_by, updated_at, " +
    "is_active, is_deleted" +
    ") VALUES (" +
    "nextval('transmission.e_transmission_id_seq'), " +     
    ":lsNo, :sessionNo, :officeId, :officePaperId, " +     
    ":forDate, :forDate, " +                                
    "NULL, NULL, NULL, NULL, " +                            
    "NULL, NULL, " +                                        
    "NULL, NULL, " +                                        
    "NULL, NULL, " +                                        
    ":userId, :now, :userId, :now, " +                       
    "TRUE, FALSE" +                                         
    ")",
    nativeQuery = true)
void saveEntityQuery(
        @Param("lsNo") String lokSabhaNo,
        @Param("sessionNo") String lokSabhaSessionNo,
        @Param("officeId") Long officeId,
        @Param("officePaperId") Long officePaperId,
        @Param("publishUuid") UUID publishUuid,                
        @Param("forDate") LocalDate forDate,
        @Param("userId") Long userIdFromAuthentication,
        @Param("now") LocalDateTime now);

}
