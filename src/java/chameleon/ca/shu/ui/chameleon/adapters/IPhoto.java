package ca.shu.ui.chameleon.adapters;

import java.net.URL;
import java.util.Date;

public interface IPhoto extends IChameleonObj {
	public String getAuthorName();

	public int getCommentsCount();

	public Date getDateTaken();

	public String getDescription();

	public String getId();

	public URL getImageUrl();

	public URL getImageUrl(int label);

	public String getOwnerId();

	public String getOwnerLocation();

	public String getOwnerName();

	public URL getOwnerProfilePicUrl();

	public String getTitle();

	public String getType();
}
