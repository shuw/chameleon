package ca.shu.ui.chameleon.flickr.adapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.flickr.FlickrAPI;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoPlace;
import com.aetrion.flickr.photos.Size;
import com.aetrion.flickr.photos.comments.Comment;
import com.aetrion.flickr.photos.comments.CommentsInterface;

public class FlickrPhoto implements IPhoto, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7932645914185908518L;

	private List<Comment> comments;

	private List<PhotoPlace> contexts;

	private Collection<Exif> exifs;

	private Collection<User> favorites;

	private Photo myPhoto;

	private Collection<Size> sizes;

	private String myPhotoId;

	private Flickr flickrAPI;

	public FlickrPhoto(String photoId) throws FlickrException {
		super();
		this.myPhotoId = photoId;
		this.flickrAPI = FlickrAPI.create();

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

	public String getTitle() {
		try {
			return getPhoto().getTitle();
		} catch (FlickrException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getType() {
		return "Flickr";
	}

	@SuppressWarnings("unchecked")
	public List<Comment> getComments() throws FlickrException {
		if (comments == null) {
			try {
				CommentsInterface commentInterface = new CommentsInterface(
						FlickrAPI.FLICKR_API_KEY, flickrAPI.getTransport());

				comments = commentInterface.getList(myPhotoId);
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
				contexts = flickrAPI.getPhotosInterface().getAllContexts(myPhotoId);
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
				exifs = flickrAPI.getPhotosInterface().getExif(myPhotoId, getPhoto().getSecret());
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
				favorites = flickrAPI.getPhotosInterface().getFavorites(myPhotoId, 30, 1);
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
				myPhoto = flickrAPI.getPhotosInterface().getInfo(myPhotoId, "");
			} catch (FlickrException e) {
				myPhoto = new Photo();
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
				sizes = flickrAPI.getPhotosInterface().getSizes(myPhotoId);
			} catch (FlickrException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sizes;
	}

	public URL getUrl() {
		try {
			return new URL(getPhoto().getUrl());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getOwnerId() {
		try {
			return getPhoto().getOwner().getId();
		} catch (FlickrException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getOwnerName() {
		try {
			return getPhoto().getOwner().getRealName();
		} catch (FlickrException e) {
			e.printStackTrace();
			return "";
		}
	}

	public URL getOwnerProfilePicUrl() {
		try {
			return new URL(getPhoto().getOwner().getBuddyIconUrl());
		} catch (FlickrException e) {
			e.printStackTrace();

		} catch (MalformedURLException e) {
			e.printStackTrace();

		}
		try {
			return new URL("");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getOwnerLocation() {
		try {
			return getPhoto().getOwner().getLocation();
		} catch (FlickrException e) {
			e.printStackTrace();
			return "";
		}
	}

}
