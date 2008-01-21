package ca.shu.ui.chameleon.adapters;

import java.net.URL;

import com.aetrion.flickr.contacts.OnlineStatus;

public interface IUser extends IChameleonObj {

	public String getId();

	public URL getProfilePictureURL();

	public String getRealName();

	public String getUserName();

	public String getAwayMessage();

	public String getLocation();

	public OnlineStatus getOnline();
}
