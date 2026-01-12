package com.dsp.templateEngine.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsp.templateEngine.util.Utils;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional("lsintraTransactionManager")
public class CircularService {

	@Autowired
	@Qualifier("lsintraJdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private HttpServletRequest httpRequest;

	@Autowired
	private Utils utils;

	@Value("${lsintra.circular.type}")
	private String circularType;

	@Value("${lsintra.branch.id}")
	private String branchId;

	public void insertCircular(UUID documentUuidEn) {

		// 1. Fetch sequence value
		Long seqValue = jdbcTemplate.queryForObject("SELECT NEXT VALUE FOR circular_notifications_seq", Long.class);

		Long nextSno = seqValue + 1;

		// 2. Build circular number
		String circularNo = "LSS/NTFS/" + nextSno + "/2026";

		// 3. Insert query
		String sql = """
				    INSERT INTO dbo.tlb_circular
				    (
				        circular_date,
				        circular_type,
				        circular_no,
				        circular_sub,
				        filepath,
				        lastupdate,
				        branchid,
				        ip_addr,
				        published_at,
				        published_by,
				        created_at,
				        created_by,
				        updated_at,
				        updated_by,
				        sub_catid,
				        status,
				        keyword,
				        uuid
				    )
				    VALUES
				    (
				        GETDATE(),
				        ?,
				        ?,
				        ?,
				        NULL,
				        GETDATE(),
				        ?,
				        ?,
				        GETDATE(),
				        ?,
				        GETDATE(),
				        ?,
				        GETDATE(),
				        ?,
				        ?,
				        ?,
				        NULL,
				        ?
				    )
				""";

		jdbcTemplate.update(sql, circularType, // circular_type
				circularNo, // circular_no
				"Summon Notification", // circular_sub
				branchId, // branchid
				utils.getLoggedInUserIp(httpRequest), // ip_addr (dummy)
				utils.getUserFullNameFromAuthentication(), // published_by
				utils.getUserFullNameFromAuthentication(), // created_by
				utils.getUserFullNameFromAuthentication(), // updated_by
				null, // sub_catid
				null, // status
				documentUuidEn.toString() // uuid
		);

		// 5. Update tlbcircular_type.cat_lastsno where id = 5
		String updateCatLastSnoSql = """
				    UPDATE dbo.tlbcircular_type
				    SET cat_lastsno = ?,
				        updated_at = GETDATE(),
				        updated_by = ?
				    WHERE id = 5
				""";

		jdbcTemplate.update(updateCatLastSnoSql, nextSno, utils.getUserFullNameFromAuthentication());
	}
}
