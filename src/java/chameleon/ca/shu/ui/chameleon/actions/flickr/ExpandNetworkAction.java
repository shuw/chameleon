package ca.shu.ui.chameleon.actions.flickr;

import java.security.InvalidParameterException;

import ca.shu.ui.chameleon.adapters.flickr.FlickrUser;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.actions.ActionException;

public class ExpandNetworkAction extends FlickrNetworkAction {

	private static final long serialVersionUID = 1L;

	private Person root;

	public ExpandNetworkAction(String actionName, int numOfDegrees,
			SocialGround chameleon, Person root) {
		super(actionName, numOfDegrees, chameleon);
		if (!(root.getModel() instanceof FlickrUser)) {
			throw new InvalidParameterException();
		}
		this.root = root;
	}

	@Override
	protected Person getPersonRoot() throws ActionException {
		return root;
	}
}