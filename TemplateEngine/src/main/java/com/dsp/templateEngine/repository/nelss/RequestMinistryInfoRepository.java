/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.repository.nelss;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dsp.templateEngine.entity.nelss.RequestMinistryInfo;

/**
 * 
 */
@Repository
public interface RequestMinistryInfoRepository extends JpaRepository<RequestMinistryInfo, Integer> {

	@Query("SELECT r FROM RequestMinistryInfo r WHERE (r.interimReply IS NOT NULL OR r.finalReply IS NOT NULL)")
	List<RequestMinistryInfo> findWithReplies();
}
