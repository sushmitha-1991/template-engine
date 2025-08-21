/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.dto;

import java.util.List;
import java.util.UUID;

public class UploadedDocumentDTO {
	private Long id;
	private Long draftId;
	private List<MemberDTO> membersData;
	private String title;
	private String applicationId;
	private Integer lokSabhaNumber;
	private Integer sessionNumber;
	private UUID documentUuid;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public UUID getDocumentUuid() {
		return documentUuid;
	}

	public void setDocumentUuid(UUID documentUuid) {
		this.documentUuid = documentUuid;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDraftId() {
		return draftId;
	}

	public void setDraftId(Long draftId) {
		this.draftId = draftId;
	}

	public List<MemberDTO> getMembersData() {
		return membersData;
	}

	public void setMembersData(List<MemberDTO> membersData) {
		this.membersData = membersData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public Integer getLokSabhaNumber() {
		return lokSabhaNumber;
	}

	public void setLokSabhaNumber(Integer lokSabhaNumber) {
		this.lokSabhaNumber = lokSabhaNumber;
	}

	public Integer getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(Integer sessionNumber) {
		this.sessionNumber = sessionNumber;
	}
}
