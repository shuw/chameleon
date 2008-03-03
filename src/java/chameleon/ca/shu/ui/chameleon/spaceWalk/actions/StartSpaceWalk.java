package ca.shu.ui.chameleon.spaceWalk.actions;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import msra_rankbase02.spacewalkv4.Channel;
import ca.shu.ui.chameleon.adapters.IUser;
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

	class WhatsNewRetriever implements Runnable {
		private SpaceUser rootUser;
		private WhatsNewSession session;

		public WhatsNewRetriever(WhatsNewSession session, SpaceUser rootUser) {
			super();
			this.session = session;
			this.rootUser = rootUser;
		}

		@Override
		public void run() {

			long timeElaspsed = 0;
			long delay = 1000;

			while (timeElaspsed < 15000) {
				Channel channel = session.getChannel();

				runUpdate(channel);

				timeElaspsed += delay;
				delay *= 1.2;

				sleep(delay);

			}

		}

		private void runUpdate(Channel rootChannel) {
			for (Channel channel : rootChannel.getFriendList().getChannel()) {

				try {
					final SpaceUser newUser = new SpaceUser(channel
							.getSpaceAlias(), new URL(channel
							.getProfilePictureUrl()), new URL(channel
							.getSpaceUrl()));

					Person person = socialGround.getPerson(newUser.getId());

					if (person == null) {

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								socialGround.addMutualRelationship(rootUser,
										newUser);
							}
						});

						/*
						 * Delay so we don't add new friends too quickly
						 */
						sleep(100);

					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}

			}

		}
	}

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

		addPerson(spaceUser);

		/*
		 * Start the what's new retriever
		 */
		(new Thread(new WhatsNewRetriever(whatsNew, spaceUser),
				"What's new retriever")).start();
	}

	protected void addPerson(final IUser user) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {

					socialGround.addPerson(user);

				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}
	}

	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
