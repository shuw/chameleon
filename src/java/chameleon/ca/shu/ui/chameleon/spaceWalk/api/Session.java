package ca.shu.ui.chameleon.spaceWalk.api;

import java.security.InvalidParameterException;

import ca.shu.ui.chameleon.exceptions.RescourceDoesNotExist;

import msra_rankbase02.spacewalkv4.Channel;
import msra_rankbase02.spacewalkv4.WebServiceSoap;

public abstract class Session {
	private final WebServiceSoap service;
	private final String sessionId;

	public Session(String sessionId) throws RescourceDoesNotExist {
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

	public String getSessionId() {
		return sessionId;
	}

	public abstract Channel getChannel();

}
