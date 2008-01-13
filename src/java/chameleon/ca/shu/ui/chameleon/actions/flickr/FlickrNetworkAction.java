package ca.shu.ui.chameleon.actions.flickr;

import java.io.IOException;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.actions.NetworkAction;
import ca.shu.ui.chameleon.adapters.IAsyncNetworkLoader;
import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.chameleon.adapters.flickr.FlickrAPI;
import ca.shu.ui.chameleon.adapters.flickr.FlickrNetworkLoader;
import ca.shu.ui.chameleon.adapters.flickr.FlickrUser;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.world.SocialGround;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;

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

	private IUser getUser(String userId, boolean ensure) throws IOException,
			SAXException, FlickrException {
		Person person = myChameleon.getPerson(userId);

		if (person == null) {
			if (ensure) {
				FlickrUser user = new FlickrUser(flickrAPI.getPeopleInterface()
						.getInfo(userId));
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

			IUser userA = getUser(userAId, ensure);
			IUser userB = getUser(userBId, ensure);

			if (userA != null && userB != null) {
				SwingUtilities.invokeAndWait(new AddRelationshipRunner(userA,
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
	private IUser userA, userB;
	private SocialGround myChameleon;

	public AddRelationshipRunner(IUser userA, IUser userB,
			SocialGround chameleon) {
		super();
		this.myChameleon = chameleon;
		this.userA = userA;
		this.userB = userB;
	}

	public void run() {
		myChameleon.addMutualRelationship(userA, userB);
	}
}
