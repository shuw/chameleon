package ca.shu.ui.chameleon.flickr.adapters;

import java.net.MalformedURLException;
import java.net.URL;

import com.aetrion.flickr.Flickr;

public class FlickrAPI {
	public static String FLICKR_API_KEY = "adb8b9c98314dc5db14a4d122fd1f9c3";

	public static String FLICKR_CACHE_FOLDER_NAME = "FlickrData";

	public static String FLICKR_ADDRESS = "http://www.flickr.com";

	public static Flickr create() {

		return new Flickr(FLICKR_API_KEY);

	}

	public static URL getUserUrl(String userName) {
		try {
			return new URL(FLICKR_ADDRESS + "/people/" + userName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			try {
				return new URL("FLICKR_ADDRESS");
			} catch (MalformedURLException e1) {
				throw new RuntimeException();
			}
		}
	}
}
