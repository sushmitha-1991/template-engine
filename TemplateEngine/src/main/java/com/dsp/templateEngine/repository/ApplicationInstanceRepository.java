package com.dsp.templateEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsp.templateEngine.entity.ApplicationInstance;

public interface ApplicationInstanceRepository extends JpaRepository<ApplicationInstance, Long>{

}
