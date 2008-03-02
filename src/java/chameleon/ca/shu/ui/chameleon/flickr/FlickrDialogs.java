package ca.shu.ui.chameleon.flickr;

import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.UserMessages.DialogException;

public class FlickrDialogs {

	public static String askUserName() throws DialogException {
		return UserMessages
				.askDialog("Please enter a Flickr username(ex. try '-shu-')");
	}

	public static String askSearchTerm() throws DialogException {
		return UserMessages
				.askDialog("Please enter search term to show most interesting photos for");
	}

}
