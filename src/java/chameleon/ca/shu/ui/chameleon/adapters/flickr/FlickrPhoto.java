package ca.shu.ui.chameleon.adapters.flickr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ca.shu.ui.chameleon.adapters.IPhoto;

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoPlace;
import com.aetrion.flickr.photos.Size;
import com.aetrion.flickr.photos.comments.Comment;

public class FlickrPhoto implements IPhoto, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7932645914185908518L;

	private static URL constructProfileImgURL(int iconserver, String userID)
			throws MalformedURLException {
		if (iconserver > 0) {

			return new URL("http://static.flickr.com/" + iconserver
					+ "/buddyicons/" + userID + ".jpg");
		} else {
			return new URL("http://www.flickr.com/images/buddyicon.jpg");
		}
	}

	// User owner;

	private List<Comment> comments;

	private List<PhotoPlace> contexts;

	private Collection<Exif> exifs;

	private Collection<User> favorites;

	private Photo myPhoto;

	private Collection<Size> sizes;

	private String myPhotoId;

	public FlickrPhoto(String photoId) throws FlickrException {
		super();
		this.myPhotoId = photoId;
		getPhoto(); // loads the photo
	}

	public String getAuthorName() {
		return myPhoto.getOwner().getUsername();
	}

	public int getCommentsCount() {
		return myPhoto.getComments();
	}

	public Date getDateTaken() {
		return myPhoto.getDateTaken();
	}

	public String getDescription() {
		return myPhoto.getDescription();
	}

	public String getId() {
		return myPhoto.getId();
	}

	public URL getImageUrl() {
		return getImageUrl(Size.MEDIUM);
	}

	public URL getImageUrl(int label) {
		Iterator<Size> it;
		try {
			it = getSizes().iterator();
		} catch (FlickrException e) {
			e.printStackTrace();
			return null;
		}
		Size size = null;

		while (it.hasNext()) {
			size = it.next();
			if (size.getLabel() == label) {
				break;
			}
		}
		if (size == null) {
			return null;
		} else {
			try {
				return new URL(size.getSource());
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	public URL getProfilePicUrl() {
		try {
			return constructProfileImgURL(myPhoto.getOwner().getIconServer(),
					myPhoto.getOwner().getId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getTitle() {
		return myPhoto.getTitle();
	}

	public String getType() {
		return "Flickr";
	}

	@SuppressWarnings("unchecked")
	public List<Comment> getComments() throws FlickrException {
		if (comments == null) {
			try {
				comments = FlickrAPI.getCommentsInterface().getList(myPhotoId);
			} catch (FlickrException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return comments;
	}

	@SuppressWarnings("unchecked")
	public List<PhotoPlace> getContexts() throws FlickrException {
		if (contexts == null) {
			try {
				contexts = FlickrAPI.getInterfaces().getPhotosInterface()
						.getAllContexts(myPhotoId);
			} catch (FlickrException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return contexts;
	}

	@SuppressWarnings("unchecked")
	public Collection<Exif> getExifs() throws FlickrException {
		if (exifs == null) {
			try {
				exifs = FlickrAPI.getInterfaces().getPhotosInterface().getExif(
						myPhotoId, getPhoto().getSecret());
			} catch (FlickrException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return exifs;
	}

	@SuppressWarnings("unchecked")
	public Collection<User> getFavorites() throws FlickrException {
		if (favorites == null) {
			try {
				favorites = FlickrAPI.getInterfaces().getPhotosInterface()
						.getFavorites(myPhotoId, 30, 1);
			} catch (FlickrException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return favorites;
	}

	public Photo getPhoto() throws FlickrException {
		if (myPhoto == null) {
			try {
				myPhoto = FlickrAPI.getInterfaces().getPhotosInterface()
						.getInfo(myPhotoId, "");
			} catch (FlickrException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return myPhoto;
	}

	@SuppressWarnings("unchecked")
	public Collection<Size> getSizes() throws FlickrException {
		if (sizes == null) {
			try {
				sizes = FlickrAPI.getInterfaces().getPhotosInterface()
						.getSizes(myPhotoId);
			} catch (FlickrException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sizes;
	}

}
