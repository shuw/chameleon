package ca.shu.ui.chameleon.spaceWalk.api;

import ca.shu.ui.chameleon.exceptions.RescourceDoesNotExist;
import msra_rankbase02.spacewalkv4.Channel;

public class WhatsNewSession extends Session {

	protected WhatsNewSession(String sessionId) throws RescourceDoesNotExist {
		super(sessionId);
	}

	public Channel getChannel() {
		return getService().getWhatsNewItems(getSessionId(), 20);

	}	
}
