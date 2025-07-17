/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsp.templateEngine.dto.nelss.NewEventsLssDTO;
import com.dsp.templateEngine.entity.nelss.NewEventsLss;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewEventsLssService {

	@PersistenceContext(unitName = "nelss")
	private EntityManager nelssEntityManager;
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


	// @Transactional("nelssTransactionManager")
	// public RequestInfo saveRequestInfo(RequestInfo requestInfo) {
	// 	nelssEntityManager.persist(requestInfo);
	// 	return requestInfo;
	// }

	// @Transactional("nelssTransactionManager")
	// public RequestMinistryInfo saveRequestMinistryInfo(RequestMinistryInfo requestMinistryInfo) {
	// 	nelssEntityManager.persist(requestMinistryInfo);
	// 	return requestMinistryInfo;
	// }

	// @Transactional("nelssTransactionManager")
	// public Integer getMaxNoticeId(int lsNo, int sessionNo) {
	// 	String sql = "SELECT MAX(ri.notice_id) FROM dbo.RequestInfo ri WHERE ri.ls_no = :lsNo AND ri.session_no = :sessionNo";
	// 	Object result = nelssEntityManager.createNativeQuery(sql).setParameter("lsNo", lsNo)
	// 			.setParameter("sessionNo", sessionNo).getSingleResult();
	// 	return result != null ? ((Number) result).intValue() : 0;
	// }

	// @Transactional("nelssTransactionManager")
	// public LokSabhaSessionDTO getLatestLokSabhaSession() {
	// 	String sql = "SELECT TOP 1 ls_no, session_no FROM dbo.LokSabhaSession ORDER BY sno DESC";
	// 	Object[] result = (Object[]) nelssEntityManager.createNativeQuery(sql).getSingleResult();

	// 	return new LokSabhaSessionDTO(((Number) result[0]).intValue(), ((Number) result[1]).intValue());
	// }

	// @Transactional("nelssTransactionManager")
	// public Integer getNoticeTypeId(String abbr) {
	// 	String sql = "SELECT notice_type_code from dbo.NoticeType where notice_short_name = :abbr";
	// 	Object result = nelssEntityManager.createNativeQuery(sql).setParameter("abbr", abbr).getSingleResult();
	// 	return result != null ? ((Number) result).intValue() : 0;
	// }

	// @Transactional("nelssTransactionManager")
	// public List<RequestMinistryInfo> findAllWithReplies() {
	// 	String sql = "SELECT * FROM dbo.RequestMinistryInfo "
	// 			+ "WHERE interim_reply IS NOT NULL OR final_reply IS NOT NULL";

	// 	return nelssEntityManager.createNativeQuery(sql, RequestMinistryInfo.class).getResultList();
	// }

}
