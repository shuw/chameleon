package ca.shu.ui.chameleon.spaceWalk.actions;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.exceptions.RescourceDoesNotExist;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.spaceWalk.SpaceWalkDialogs;
import ca.shu.ui.chameleon.spaceWalk.api.SpaceWalkAPI;
import ca.shu.ui.chameleon.spaceWalk.api.WhatsNewSession;
import ca.shu.ui.chameleon.spaces.SpacesAPI;
import ca.shu.ui.chameleon.spaces.objects.SpaceUser;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UserMessages.DialogException;

public class StartSpaceWalk extends StandardAction {

	private static final long serialVersionUID = 1L;

	private SocialGround socialGround;

	public StartSpaceWalk(String description, SocialGround socialGround) {
		super(description, false);

		this.socialGround = socialGround;
	}

	@Override
	protected void action() throws ActionException {
		String userAlias;
		try {
			userAlias = SpaceWalkDialogs.askUserAlias();
		} catch (DialogException e) {
			throw new UserCancelledException();
		}
		WhatsNewSession whatsNew;
		SpaceUser spaceUser;
		try {
			spaceUser = SpacesAPI.getSpaceUser(userAlias);

			whatsNew = SpaceWalkAPI.getWhatsNewSession(userAlias);
		} catch (RescourceDoesNotExist e) {
			throw new ActionException("Could not open what's new");
		}

		final SpaceUser spaceUserFinal = spaceUser;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Person person = socialGround.addPerson(spaceUserFinal);
				person.setAnchored(true);

			}
		});

		/*
		 * Start the what's new retriever
		 */
		(new Thread(
				new ChannelUpdater(whatsNew, socialGround, spaceUser, 15000),
				"What's new retriever")).start();
	}

}
