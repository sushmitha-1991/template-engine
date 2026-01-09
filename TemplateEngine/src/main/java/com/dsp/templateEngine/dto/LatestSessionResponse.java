package com.dsp.templateEngine.dto;

import java.util.List;

import lombok.Data;

@Data
public class LatestSessionResponse {
	private DataNode data;

	@Data
	public static class DataNode {
		private List<Session> session;
		private List<LokSabha> loksabha;
	}

	@Data
	public static class Session {
		private String sessionNumber;
	}

	@Data
	public static class LokSabha {
		private String lokSabhaNumber;
	}
}
