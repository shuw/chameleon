package ca.shu.ui.chameleon.spaceWalk.actions;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import msra_rankbase02.spacewalkv4.Channel;
import msra_rankbase02.spacewalkv4.Item;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.objects.PersonItem;
import ca.shu.ui.chameleon.spaceWalk.api.Session;
import ca.shu.ui.chameleon.spaceWalk.objects.BlogItemInfo;
import ca.shu.ui.chameleon.spaces.objects.SpaceUser;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.util.Util;

public class ChannelUpdater implements Runnable {

	private SpaceUser myUser;
	private Session session;
	private SocialGround socialGround;
	private long runForTimeMS;

	public ChannelUpdater(Session session, SocialGround ground,
			SpaceUser rootUser, long runForTimeMS) {
		super();
		this.session = session;
		this.myUser = rootUser;
		this.socialGround = ground;
		this.runForTimeMS = runForTimeMS;
	}

	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				socialGround.addPerson(myUser).setAnchored(true);
			}

		});

		long timeElaspsed = 0;
		long delay = 1000;

		while (timeElaspsed < runForTimeMS) {
			Channel myChannel = session.getChannel();

			runUpdate(myChannel, myUser);

			timeElaspsed += delay;
			delay *= 1.2;

			Util.sleep(delay);

		}

	}

	/**
	 * Recursive function to go through social graph embedded in Channel class
	 * 
	 * @param rootChannel
	 */
	private void runUpdate(Channel rootChannel, SpaceUser rootUser) {
		if (rootChannel != null && rootChannel.getFriendList() != null) {
			for (Channel channel : rootChannel.getFriendList().getChannel()) {
				Person person;
				try {
					person = ensureChannel(channel, rootUser);

					if (!person.getId().equals(rootUser.getId())) {
						if (person != null) {
							for (Item item : channel.getItemList().getItem()) {
								ensureItem(person, item);
							}

						}

					}
					if (person.getModel() instanceof SpaceUser) {
						runUpdate(channel, (SpaceUser) person.getModel());
					} else {
						(new Exception()).printStackTrace();
					}

				} catch (MalformedURLException e) {
					Util
							.debugMsg("Could not add person because his profile picture could not be resolved");
				}

			}
		}

	}

	private void ensureItem(final Person person, Item item) {
		try {
			final BlogItemInfo blogInfo = new BlogItemInfo(session
					.getSessionId(), item.getItemId(), item.getItemTitle(),
					item.getBlogContents(), new URL(item.getItemUrl()));

			final PersonItem personItem = person.getItem(blogInfo.getId());
			if (personItem == null) {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {

						@Override
						public void run() {
							person.addItem(blogInfo);
						}

					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private Person ensureChannel(Channel channel, final SpaceUser siblingUser)
			throws MalformedURLException {

		String profileUrl = channel.getProfilePictureUrl();
		if (profileUrl == null || "".equals(profileUrl)) {
			profileUrl = "http://l.yimg.com/www.flickr.com/images/buddyicon.jpg";
		}

		final SpaceUser newUser = new SpaceUser(channel.getSpaceAlias(),
				new URL(profileUrl), new URL(channel.getSpaceUrl()));

		Person person = socialGround.getPerson(newUser.getId());

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					socialGround.addMutualRelationship(siblingUser, newUser);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}

		/*
		 * Delay so we don't add new friends too quickly
		 */
		Util.sleep(500);

		person = socialGround.getPerson(newUser.getId());

		return person;

	}
}
