package ca.shu.ui.chameleon.adapters.flickr;

import java.io.IOException;
import java.util.Collection;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.adapters.IAsyncNetworkLoader;
import ca.shu.ui.chameleon.adapters.INetworkListener;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.contacts.Contact;

public class FlickrNetworkLoader implements IAsyncNetworkLoader {

	static final int MAX_FRIENDS_OPTIONAL_LIMIT = 0; // Friends to retrieve

	private Flickr flickrAPI;

	public FlickrNetworkLoader() {
		super();
		flickrAPI = FlickrAPI.create();
	}

	/**
	 * @param rootUser
	 *            User to load friends for
	 * @param depth
	 *            Number of levels within the social network from the origin
	 * @throws FlickrException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private synchronized void loadRecursive(String contactId, int depth,
			int maxDepth, INetworkListener networkListener) throws IOException,
			SAXException, FlickrException {

		depth++;

		if (depth > maxDepth) {
			return;
		}

		if (isClosed) {
			return;
		}

		Collection<Contact> contacts = flickrAPI.getContactsInterface()
				.getPublicList(contactId);

		for (Contact contact : contacts) {
			networkListener.acceptNewConnection(contactId, contact.getId(),
					depth);
		}

		for (Contact contact : contacts) {
			loadRecursive(contact.getId(), depth, maxDepth, networkListener);
		}
	}

	public void loadNetworkAsync(String userIdRoot, int degrees,
			INetworkListener networkListener) {
		(new NetworkLoaderThread(userIdRoot, degrees, networkListener)).start();

	}

	class NetworkLoaderThread extends Thread {
		private INetworkListener networkListener;
		private int degrees;
		private String userIdRoot;

		public NetworkLoaderThread(String userIdRoot, int degrees,
				INetworkListener networkListener) {
			super("Network loader");
			this.userIdRoot = userIdRoot;
			this.degrees = degrees;
			this.networkListener = networkListener;
		}

		public void run() {
			try {
				loadRecursive(userIdRoot, 0, degrees, networkListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isClosed = false;

	public void close() {
		isClosed = true;
	}

}
