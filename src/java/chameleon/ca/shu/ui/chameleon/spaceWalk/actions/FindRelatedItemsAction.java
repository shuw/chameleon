package ca.shu.ui.chameleon.spaceWalk.actions;

import ca.shu.ui.chameleon.exceptions.RescourceDoesNotExist;
import ca.shu.ui.chameleon.spaceWalk.api.RelatedItemsSession;
import ca.shu.ui.chameleon.spaceWalk.objects.BlogItemInfo;
import ca.shu.ui.chameleon.spaces.objects.SpaceUser;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class FindRelatedItemsAction extends StandardAction {
	private BlogItemInfo item;

	private SocialGround ground;
	private SpaceUser rootUser;

	public FindRelatedItemsAction(String description, SocialGround ground,
			SpaceUser rootUser, BlogItemInfo item) {
		super(description, false);
		this.item = item;
		this.ground = ground;
		this.rootUser = rootUser;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void action() throws ActionException {
		try {
			RelatedItemsSession relatedItemsSession = new RelatedItemsSession(
					item.getSessionId(), item.getId());

			(new Thread(new ChannelUpdater(relatedItemsSession, ground,
					rootUser, 30000), "Related item retriever")).start();

		} catch (RescourceDoesNotExist e) {
			throw new ActionException(
					"Could not get related items because rescource was unavailable", e);
		}

	}
}
