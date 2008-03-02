package ca.shu.ui.chameleon.spaceWalk.actions;

import ca.shu.ui.chameleon.exceptions.RescourceDoesNotExist;
import ca.shu.ui.chameleon.flickr.adapters.FlickrPhotoSource;
import ca.shu.ui.chameleon.spaceWalk.SpaceWalkDialogs;
import ca.shu.ui.chameleon.spaceWalk.api.SpaceWalkAPI;
import ca.shu.ui.chameleon.spaceWalk.api.WhatsNewSession;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UserMessages.DialogException;

public class StartSpaceWalk extends StandardAction {

	private static final long serialVersionUID = 1L;

	public StartSpaceWalk(String description) {
		super(description);
	}

	@Override
	protected void action() throws ActionException {
		try {
			String userName = SpaceWalkDialogs.askUserAlias();

			try {
				WhatsNewSession whatsNew = SpaceWalkAPI
						.getWhatsNewSession(userName);
				
				
			} catch (RescourceDoesNotExist e) {
				throw new ActionException("Could not find alias");
			}

		} catch (DialogException e) {
			throw new UserCancelledException();
		}
	}

}