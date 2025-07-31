package com.dsp.templateEngine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsp.templateEngine.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long>{

  String findAbbrById(Long id);

}
