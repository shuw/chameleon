package ca.shu.ui.chameleon.actions.flickr;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.Chameleon;
import ca.shu.ui.chameleon.adapters.flickr.FlickrAPI;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs;
import ca.shu.ui.chameleon.adapters.flickr.FlickrUser;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs.FlickrDialogException;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.contacts.Contact;
import com.aetrion.flickr.people.User;

public class NetworkBuilder extends StandardAction {

	private static final long serialVersionUID = 1L;

	private static User loadUser(String userName) throws IOException,
			SAXException, FlickrException {

		return FlickrAPI.getInterfaces().getPeopleInterface().findByUsername(
				userName);

	}

	public NetworkBuilder(String actionName) {
		super("Open Social Network", actionName, false);

	}

	private void loadSocialNetwork(String userName) throws IOException,
			SAXException, FlickrException {

		// resolve the home user in the action thread
		User user = loadUser(userName);

		// load friends in new thread
		(new Thread(new FriendLoader(user))).start();
	}

	@Override
	protected void action() throws ActionException {
		try {
			String userName = FlickrDialogs.askUserName();

			try {
				loadSocialNetwork(userName);
			} catch (FlickrException e) {
				throw new ActionException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (FlickrDialogException e1) {
			throw new UserCancelledException();
		}

	}

}

class AddFlickrUserRunner implements Runnable {
	FlickrUser flickrUser;

	public AddFlickrUserRunner(FlickrUser flickrUser) {
		super();
		this.flickrUser = flickrUser;
	}

	public void run() {
		Chameleon.getInstance().addPerson(flickrUser);
	}

}

class AddRelationshipRunner implements Runnable {
	String id_personA, id_personB;

	public AddRelationshipRunner(String id_personA, String id_personB) {
		super();
		this.id_personA = id_personA;
		this.id_personB = id_personB;
	}

	public void run() {
		Chameleon.getInstance().addRelationship(id_personA, id_personB);

	}
}

class FriendLoader implements Runnable {
	static final int MAX_DEPTH = 2; // Nth degree social network
	static final int MAX_FRIENDS_OPTIONAL_LIMIT = 0; // Friends to retrieve

	// FoF

	public static void addRelationship(String id_personA, String id_personB) {
		try {
			SwingUtilities.invokeAndWait((new AddRelationshipRunner(id_personA,
					id_personB)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}
	}

	public static void addUser(FlickrUser flickrUser) throws IOException,
			SAXException, FlickrException {

		try {
			SwingUtilities.invokeAndWait((new AddFlickrUserRunner(flickrUser)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	private User origin;

	public FriendLoader(User origin) {
		super();
		this.origin = origin;
	}

	/**
	 * @param rootUser
	 *            User to load friends for
	 * @param depth
	 *            Number of levels within the social network from the origin
	 * @throws FlickrException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void loadRecursive(User rootUser, int depth, boolean limitFriends)
			throws IOException, SAXException, FlickrException {

		if (depth >= MAX_DEPTH) {
			return;
		}

		Collection<Contact> contacts = FlickrAPI.getInterfaces()
				.getContactsInterface().getPublicList(rootUser.getId());

		Collection<Contact> foreignContacts = new ArrayList<Contact>(contacts
				.size());

		ArrayList<User> contactUsers = new ArrayList<User>(contacts.size());

		// first find contact who already exist in the world
		for (Contact contact : contacts) {

			FlickrUser flickrUser = Chameleon.getInstance().getPerson(
					contact.getId());

			if (flickrUser != null) {
				User contactUser = flickrUser.getModel();
				contactUsers.add(contactUser);
				addRelationship(rootUser.getId(), contactUser.getId());
			} else {
				foreignContacts.add(contact);
			}
		}

		// Get foreign contacts
		int countCount = 0;
		for (Contact contact : foreignContacts) {
			User contactUser = null;

			if (limitFriends && countCount++ >= MAX_FRIENDS_OPTIONAL_LIMIT) {
				break;
			}

			FlickrUser flickrUser = Chameleon.getInstance().getPerson(
					contact.getId());

			if (flickrUser != null) {
				contactUser = flickrUser.getModel();
			}

			if (contactUser == null) {
				// load user from contact
				contactUser = FlickrAPI.getInterfaces().getPeopleInterface()
						.getInfo(contact.getId());
				flickrUser = new FlickrUser(contactUser);

				FlickrUser rootFlickrUser = Chameleon.getInstance().getPerson(
						rootUser.getId());

				// Move the new user to the position of the root user
				if (rootFlickrUser != null) {
					flickrUser.setOffset(rootFlickrUser.getOffsetReal());
				}
				addUser(flickrUser);
			}
			contactUsers.add(contactUser);
			addRelationship(rootUser.getId(), contactUser.getId());
		}

		for (User contactUser : contactUsers) {
			loadRecursive(contactUser, depth + 1, true);
		}
	}

	public void run() {
		try {
			FlickrUser flickrUser = new FlickrUser(origin);
			addUser(flickrUser);

			loadRecursive(origin, 0, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
