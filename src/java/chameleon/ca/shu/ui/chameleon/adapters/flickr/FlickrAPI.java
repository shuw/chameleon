package ca.shu.ui.chameleon.adapters.flickr;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.comments.CommentsInterface;

public class FlickrAPI {
	public static String FLICKR_API_KEY = "adb8b9c98314dc5db14a4d122fd1f9c3";

	public static String FLICKR_CACHE_FOLDER_NAME = "FlickrData";

	public static Flickr create() {

		return new Flickr(FLICKR_API_KEY);

	}

}
