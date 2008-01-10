package ca.shu.ui.chameleon.actions.flickr;

import java.io.IOException;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.Chameleon;
import ca.shu.ui.chameleon.actions.AbstractNetworkAction;
import ca.shu.ui.chameleon.adapters.IAsyncNetworkLoader;
import ca.shu.ui.chameleon.adapters.flickr.FlickrAPI;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs;
import ca.shu.ui.chameleon.adapters.flickr.FlickrNetworkLoader;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs.FlickrDialogException;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.UserCancelledException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.User;

public class FlickrNetworkAction extends AbstractNetworkAction {

	private static final long serialVersionUID = 1L;
	Chameleon myChameleon;
	private Flickr flickrAPI;

	public FlickrNetworkAction(String actionName, int numOfDegrees,
			Chameleon chameleon) {
		super(actionName, numOfDegrees);
		flickrAPI = FlickrAPI.create();
		this.myChameleon = chameleon;
	}

	private static User loadUser(String userName) throws IOException,
			SAXException, FlickrException {

		return FlickrAPI.create().getPeopleInterface().findByUsername(userName);

	}

	@Override
	protected String getRootId() throws ActionException {
		try {
			String userName = FlickrDialogs.askUserName();
			// resolve the home user in the action thread
			User user;
			try {
				user = loadUser(userName);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (SAXException e) {
				e.printStackTrace();
				return null;
			} catch (FlickrException e) {
				throw new ActionException(e.getMessage());
			}

			Person uiPerson = new Person(user);
			myChameleon.addPerson(uiPerson);

			return user.getId();
		} catch (FlickrDialogException e1) {
			throw new UserCancelledException();
		}

	}

	@Override
	protected IAsyncNetworkLoader getNetworkLoader() {
		return new FlickrNetworkLoader();
	}

	private User getUser(String userId, boolean ensure) throws IOException,
			SAXException, FlickrException {
		Person person = myChameleon.getPerson(userId);

		if (person == null) {
			if (ensure) {
				User user = flickrAPI.getPeopleInterface().getInfo(userId);
				return user;
			} else {
				return null;
			}
		} else {
			return person.getModel();
		}

	}

	public void acceptNewConnection(String userAId, String userBId,
			int degreesFromRoot) {

		try {
			boolean ensure = true;
			if (degreesFromRoot > 1) {
				ensure = false;
			}

			User userA = getUser(userAId, ensure);
			User userB = getUser(userBId, ensure);

			if (userA != null && userB != null) {
				SwingUtilities.invokeLater(new AddRelationshipRunner(userA,
						userB, myChameleon));
			}
		} catch (FlickrException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

class AddFlickrUserRunner implements Runnable {
	User flickrUser;

	public AddFlickrUserRunner(User flickrUser) {
		super();
		this.flickrUser = flickrUser;
	}

	public void run() {
		Person person = new Person(flickrUser);
		Chameleon.getInstance().addPerson(person);
	}

}

class AddRelationshipRunner implements Runnable {
	private User userA, userB;
	private Chameleon myChameleon;

	public AddRelationshipRunner(User userA, User userB, Chameleon chameleon) {
		super();
		this.myChameleon = chameleon;
		this.userA = userA;
		this.userB = userB;
	}

	public void run() {

		Person personA = myChameleon.getPerson(userA.getId());
		if (personA == null) {
			personA = new Person(userA);
			myChameleon.addPerson(personA);
		}
		Person personB = myChameleon.getPerson(userB.getId());
		if (personB == null) {
			personB = new Person(userB);
			myChameleon.addPerson(personB);
		}

		myChameleon.addRelationship(personA, personB);

	}
}
