package ca.shu.ui.chameleon.actions.flickr;

import java.io.IOException;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.actions.NetworkAction;
import ca.shu.ui.chameleon.adapters.IAsyncNetworkLoader;
import ca.shu.ui.chameleon.adapters.flickr.FlickrAPI;
import ca.shu.ui.chameleon.adapters.flickr.FlickrNetworkLoader;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.activities.Fader;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.User;

public abstract class FlickrNetworkAction extends NetworkAction {

	private static final long serialVersionUID = 1L;
	SocialGround myChameleon;
	private Flickr flickrAPI;

	public FlickrNetworkAction(String actionName, int numOfDegrees,
			SocialGround chameleon) {
		super(actionName, numOfDegrees);
		flickrAPI = FlickrAPI.create();
		this.myChameleon = chameleon;
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

class AddRelationshipRunner implements Runnable {
	private User userA, userB;
	private SocialGround myChameleon;

	public AddRelationshipRunner(User userA, User userB, SocialGround chameleon) {
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

			// If this is a new person being created, we move it to the person
			// it was related to

			personB.setOffset(personA.getOffset().getX() + 20, personA
					.getOffset().getY() + 20);
			personB.setTransparency(0f);
			Fader fader = new Fader(personB, 1000, 1f);
			myChameleon.addActivity(fader);
			myChameleon.addPerson(personB);
		}

		myChameleon.addRelationship(personA, personB);

	}
}
