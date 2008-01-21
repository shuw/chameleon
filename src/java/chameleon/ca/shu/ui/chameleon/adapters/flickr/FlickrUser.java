package ca.shu.ui.chameleon.adapters.flickr;

import java.net.MalformedURLException;
import java.net.URL;

import ca.shu.ui.chameleon.adapters.IUser;

import com.aetrion.flickr.contacts.OnlineStatus;
import com.aetrion.flickr.people.User;

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

	public String getAwayMessage() {
		return user.getAwayMessage();
	}

	public String getLocation() {
		return user.getLocation();
	}

	public OnlineStatus getOnline() {
		return user.getOnline();
	}
}
