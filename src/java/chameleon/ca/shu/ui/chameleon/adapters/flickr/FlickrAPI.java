package ca.shu.ui.chameleon.adapters.flickr;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.comments.CommentsInterface;

public class FlickrAPI {
	private static Flickr flickr;

	public static String FLICKR_API_KEY = "adb8b9c98314dc5db14a4d122fd1f9c3";

	public static String FLICKR_CACHE_FOLDER_NAME = "FlickrData";

	public static CommentsInterface getCommentsInterface() {
		return new CommentsInterface(FLICKR_API_KEY, getInterfaces()
				.getTransport());
	}

	public static Flickr getInterfaces() {
		if (flickr == null) {
			flickr = new Flickr(FLICKR_API_KEY);
		}
		return flickr;
	}

}
