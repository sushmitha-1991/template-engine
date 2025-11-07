/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsp.templateEngine.dto.lssupdate.LssUpdatesDTO;
import com.dsp.templateEngine.dto.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.lssupdate.LssUpdates;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewEventsLssService {

	@PersistenceContext(unitName = "nelss")
	private EntityManager nelssEntityManager;
	@PersistenceContext(unitName = "lssupdate")
	private EntityManager lssupdateEntityManager;
	@Transactional("nelssTransactionManager")
	public NewEventsLss saveNewEvent(NewEventsLssDTO dto) {
    NewEventsLss entity = new NewEventsLss();
    entity.setEtitle(dto.getEtitle());
    entity.setHtitle(dto.getHtitle());
    entity.setEfile(dto.getEfile());
    entity.setHfile(dto.getHfile());
    entity.setEurl(dto.getEurl());
    entity.setHurl(dto.getHurl());
    entity.setEdate(dto.getEdate());
    entity.setEtype(dto.getEtype());
    entity.setHtype(dto.getHtype());
    entity.setIsNew(dto.getIsNew());
    entity.setIsActive(dto.getIsActive());
    
    nelssEntityManager.persist(entity);
    return entity;
}

@Transactional("lssupdateTransactionManager")
	public LssUpdates saveLssUpdates(LssUpdatesDTO dto) {
    LssUpdates entity = new LssUpdates();
    entity.setTitle(dto.getTitle());
    entity.setFile(dto.getFile());
    entity.setUrl(dto.getUrl());
    entity.setDate(dto.getDate());
    entity.setType(dto.getType());
    entity.setIsNew(dto.getIsNew());
    entity.setIsActive(dto.getIsActive());
    entity.setUsername(dto.getUsername());
    entity.setUserId(dto.getUserId());
    entity.setCreatedAt(dto.getCreatedAt());
    entity.setCreatedBy(dto.getCreatedBy());
    entity.setUpdatedAt(dto.getUpdatedAt());
    entity.setUpdatedBy(dto.getUpdatedBy());
    entity.setIpAddr(dto.getIpAddr());
    entity.setLocale(dto.getLocale());
    entity.setPublishedAt(dto.getPublishedAt());
    entity.setPublishedBy(dto.getPublishedBy());
    entity.setUuid(dto.getUuid());
	
    lssupdateEntityManager.persist(entity);
    return entity;
}


}
