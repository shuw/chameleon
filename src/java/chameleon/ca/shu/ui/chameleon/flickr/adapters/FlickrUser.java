package ca.shu.ui.chameleon.flickr.adapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.chameleon.flickr.FlickrAPI;
import ca.shu.ui.lib.world.Searchable.SearchValuePair;

import com.aetrion.flickr.contacts.OnlineStatus;
import com.aetrion.flickr.people.User;

public class FlickrUser implements IUser {

	private Collection<SearchValuePair> searchableValues;

	private User user;

	public FlickrUser(User user) {
		super();
		this.user = user;
		init();
	}

	public void constructTooltips(TooltipBuilder builder) {

		for (SearchValuePair searchValuePair : searchableValues) {
			builder.addProperty(searchValuePair.getName(), searchValuePair
					.getValue());
		}

		String onlineStatus;
		if (getOnline() != null) {
			onlineStatus = getOnline().toString();
		} else {
			onlineStatus = "unknown";
		}

		builder.addProperty("Online status", onlineStatus);
	}

	public String getAwayMessage() {
		return user.getAwayMessage();
	}

	public String getDisplayName() {
		if (user.getRealName() != null) {
			return user.getRealName();
		} else {
			return user.getUsername();
		}
	}

	public String getId() {
		return user.getId();
	}

	public String getLocation() {
		return user.getLocation();
	}

	public OnlineStatus getOnline() {
		return user.getOnline();
	}

	public URL getProfilePictureURL() {
		try {
			return new URL(user.getBuddyIconUrl());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Collection<SearchValuePair> getSearchableValues() {
		return searchableValues;
	}

	public URL getURL() {

		return FlickrAPI.getUserUrl(user.getId());
	}

	private void init() {
		LinkedList<SearchValuePair> sValues = new LinkedList<SearchValuePair>();
		sValues.add(new SearchValuePair("Real Name", user.getRealName()));
		sValues.add(new SearchValuePair("User Name", user.getUsername()));
		sValues.add(new SearchValuePair("User Id", user.getId()));
		sValues.add(new SearchValuePair("Location", user.getLocation()));
		sValues.add(new SearchValuePair("Away Message", user.getAwayMessage()));

		this.searchableValues = new ArrayList<SearchValuePair>(sValues);
	}

}
