package ca.shu.ui.chameleon.adapters.flickr;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.adapters.IAsyncNetworkLoader;
import ca.shu.ui.chameleon.adapters.INetworkListener;
import ca.shu.ui.chameleon.objects.Person;

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

	public void loadNetworkAsync(Person userRoot, int degrees,
			INetworkListener networkListener) {
		(new NetworkLoaderThread(userRoot, degrees, networkListener)).start();

	}

	class NetworkLoaderThread extends Thread {
		private INetworkListener networkListener;
		private int degrees;
		private Person userRoot;

		public NetworkLoaderThread(Person userRoot, int degrees,
				INetworkListener networkListener) {
			super("Network loader");
			this.userRoot = userRoot;
			this.degrees = degrees;
			this.networkListener = networkListener;
		}

		public void run() {
			try {
				loadNetwork();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		public void loadNetwork() throws InterruptedException,
				InvocationTargetException {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					userRoot.setPositionLocked(true);
				}
			});

			try {
				loadRecursive(userRoot.getId(), 0, degrees, networkListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					userRoot.setPositionLocked(false);
				}
			});
		}
	}

	private boolean isClosed = false;

	public void close() {
		isClosed = true;
	}

}
