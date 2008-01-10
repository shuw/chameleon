package ca.shu.ui.chameleon.actions.flickr;

import java.io.IOException;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.adapters.flickr.FlickrAPI;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs.FlickrDialogException;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.UserCancelledException;

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.User;

public class LoadNetworkAction extends FlickrNetworkAction {

	private static final long serialVersionUID = 1L;

	private static User loadUser(String userName) throws IOException,
			SAXException, FlickrException {
		return FlickrAPI.create().getPeopleInterface().findByUsername(userName);
	}

	public LoadNetworkAction(String actionName, int numOfDegrees,
			SocialGround chameleon) {
		super(actionName, numOfDegrees, chameleon);
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

}