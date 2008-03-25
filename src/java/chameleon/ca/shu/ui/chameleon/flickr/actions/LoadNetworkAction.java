package ca.shu.ui.chameleon.flickr.actions;

import java.io.IOException;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.flickr.FlickrAPI;
import ca.shu.ui.chameleon.flickr.FlickrDialogs;
import ca.shu.ui.chameleon.flickr.adapters.FlickrUser;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UserMessages.DialogException;

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
	protected Person getPersonRoot() throws ActionException {
		try {
			String userName = FlickrDialogs.askUserName();

			// resolve the home user in the action thread
			FlickrUser user;
			try {
				user = new FlickrUser(loadUser(userName));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (SAXException e) {
				throw new ActionException("Site down: " + e.getMessage(), e);
			} catch (FlickrException e) {
				throw new ActionException(e.getMessage(), e);
			}

			Person uiPerson = socialGround.addPerson(user);

			return uiPerson;
		} catch (DialogException e1) {
			throw new UserCancelledException();
		} finally {
		}

	}
}