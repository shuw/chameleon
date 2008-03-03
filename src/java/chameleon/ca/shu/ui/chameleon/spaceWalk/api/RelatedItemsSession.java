package ca.shu.ui.chameleon.spaceWalk.api;

import msra_rankbase02.spacewalkv4.Channel;
import ca.shu.ui.chameleon.exceptions.RescourceDoesNotExist;

public class RelatedItemsSession extends Session {

	public RelatedItemsSession(String sessionId, String itemId)
			throws RescourceDoesNotExist {
		super(sessionId);

		if (!getService().initRelatedItems(sessionId, itemId)) {
			throw new RescourceDoesNotExist();
		}
	}

	@Override
	public Channel getChannel() {
		return getService().getRelatedItems(getSessionId(), 10);
	}
}
