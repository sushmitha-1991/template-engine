package com.dsp.templateEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsp.templateEngine.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long>{

	 @Query("SELECT a.abbr FROM Application a WHERE a.id = :id")
	 String findAbbrById(@Param("id") Long id);
}
