
package com.dsp.templateEngine.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsp.templateEngine.entity.enoticeboard.NewEventsRss;
import com.dsp.templateEngine.entity.enoticeboard.NewEventsRssDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ENoticeBoardService {

	@PersistenceContext(unitName = "enotice")
	private EntityManager enoticeEntityManager;
	@Transactional("enoticeTransactionManager")
public NewEventsRss saveNewEvent(NewEventsRssDTO dto) {
    NewEventsRss entity = new NewEventsRss();
    entity.setTitle(dto.getTitle());
    entity.setLanguage(dto.getLanguage());
    entity.setAddImageOrNot(dto.getAddImageOrNot());
    entity.setStartDate(dto.getStartDate());
    entity.setEndDate(dto.getEndDate());
    entity.setFileName(dto.getFileName());
     entity.setPdfFileUrl(dto.getPdfFileUrl());
    entity.setImageFileUrl(dto.getImageFileUrl());
    entity.setType(dto.getType());
      entity.setVenue(dto.getVenue());
    entity.setSection(dto.getSection());
    entity.setFileNo(dto.getFileNo());

    enoticeEntityManager.persist(entity);
    return entity;
}

}
