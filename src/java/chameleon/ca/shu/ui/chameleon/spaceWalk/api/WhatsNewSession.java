package ca.shu.ui.chameleon.spaceWalk.api;

import msra_rankbase02.spacewalkv4.Channel;

public class WhatsNewSession extends Session {

	protected WhatsNewSession(String sessionId) {
		super(sessionId);
	}

	public Channel getChannel() {
		return getService().getWhatsNewItems(getSessionId(), 20);

	}
	

	
}
