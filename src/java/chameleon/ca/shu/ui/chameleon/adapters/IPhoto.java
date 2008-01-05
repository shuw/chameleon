package ca.shu.ui.chameleon.adapters;

import java.net.URL;
import java.util.Date;

public interface IPhoto {
	public String getAuthorName();

	public int getCommentsCount();

	public Date getDateTaken();

	public String getDescription();

	public String getId();

	public URL getImageUrl();

	public URL getImageUrl(int label);

	public URL getProfilePicUrl();

	public String getTitle();

	public String getType();
}
