package com.dsp.templateEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.RegionalTranslationLog;

@Repository
public interface RegionalTranslationLogRepository extends JpaRepository<RegionalTranslationLog, Long> {
	
	boolean existsByApplicationInstanceId(Long applicationInstanceId);

}
