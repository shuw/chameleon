package ca.shu.ui.chameleon.adapters.flickr;

import java.net.MalformedURLException;
import java.net.URL;

import com.aetrion.flickr.people.User;

import ca.shu.ui.chameleon.adapters.IUser;

public class FlickrUser implements IUser {

	private User user;

	public FlickrUser(User user) {
		super();
		this.user = user;
	}

	public String getId() {
		return user.getId();
	}

	public URL getProfilePictureURL() {
		try {
			return new URL(user.getBuddyIconUrl());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getRealName() {
		if (user.getRealName() != null) {
			return user.getRealName();
		} else {
			return getUserName();
		}
	}

	public String getUserName() {
		return user.getUsername();
	}

	public URL getUrl() {

		return FlickrAPI.getUserUrl(user.getId());
	}
}
