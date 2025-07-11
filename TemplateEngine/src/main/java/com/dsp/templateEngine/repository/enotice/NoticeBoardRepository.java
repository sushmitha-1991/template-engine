package com.dsp.templateEngine.repository.enotice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.enoticeboard.NewEventsRss;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NewEventsRss, Long> {
}