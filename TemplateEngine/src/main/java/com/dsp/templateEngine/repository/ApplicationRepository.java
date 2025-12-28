package com.dsp.templateEngine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsp.templateEngine.entity.Application;

import jakarta.transaction.Transactional;

public interface ApplicationRepository extends JpaRepository<Application, Long>{

	 @Query("SELECT a.abbr FROM Application a WHERE a.id = :id") 
	 String findAbbrById(@Param("id") Long id);
	
    //  Optional<Application> findByDocumentDisplayNameAndHouse(String category, String house);
    //  Optional<Application> findByAbbrAndHouse(String category, String house);
	 
	@Query(value = "SELECT * FROM master.applications  "+
	"WHERE document_display_name = :displayName Limit 1", nativeQuery=true)
	Optional<Application> findByDocumentDisplayName(@Param("displayName") String displayName);

	@Query(value = "SELECT * FROM master.applications  "+
	" WHERE abbr = :abbr limit 1 ",nativeQuery = true)
	Optional<Application> findByAbbr(@Param("abbr") String abbr);

}
