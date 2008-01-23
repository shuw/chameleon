package ca.shu.ui.chameleon.adapters.flickr;

import javax.swing.JOptionPane;

import ca.shu.ui.chameleon.Chameleon;
import ca.shu.ui.lib.exceptions.UIException;

public class FlickrDialogs {

	public static String askDialog(String dialogMessage) throws FlickrDialogException {
		String userName = JOptionPane.showInputDialog(Chameleon.getInstance(), dialogMessage);

		if (userName == null || userName.compareTo("") == 0) {
			throw new FlickrDialogException();
		}
		return userName;
	}

	public static String askUserName() throws FlickrDialogException {
		return askDialog("Please enter a Flickr username(ex. try '-shu-')");
	}

	public static String askSearchTerm() throws FlickrDialogException {
		return askDialog("Please enter search term to show most interesting photos for");
	}

	
	public static class FlickrDialogException extends UIException {

		private static final long serialVersionUID = 1L;

		public FlickrDialogException() {
			super("Dialog cancelled");
		}

		public FlickrDialogException(String arg0) {
			super(arg0);
		}
	}
}
