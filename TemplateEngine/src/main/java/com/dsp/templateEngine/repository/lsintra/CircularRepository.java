package com.dsp.templateEngine.repository.lsintra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.lsintra.Circular;

@Repository
public interface CircularRepository extends JpaRepository<Circular, Long> {

}
