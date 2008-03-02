package ca.shu.ui.chameleon.spaceWalk.api;

import java.security.InvalidParameterException;

import msra_rankbase02.spacewalkv4.WebServiceSoap;

public class Session {
	private final WebServiceSoap service;
	private String sessionId;

	public Session(String sessionId) {
		super();
		if (sessionId == null || "".equals(sessionId)) {
			throw new InvalidParameterException();
		}

		this.sessionId = sessionId;
		service = SpaceWalkAPI.createService();
	}

	public WebServiceSoap getService() {
		return service;
	}

}
