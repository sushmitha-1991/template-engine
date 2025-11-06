package com.dsp.templateEngine.repository.lssupdate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.lssupdate.LssUpdates;

@Repository
public interface LssUpdateRepository extends JpaRepository<LssUpdates, Long> {
}

