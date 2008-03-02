package ca.shu.ui.chameleon.flickr.adapters;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.adapters.IAsyncNetworkLoader;
import ca.shu.ui.chameleon.adapters.INetworkListener;
import ca.shu.ui.chameleon.flickr.FlickrAPI;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.lib.objects.activities.TrackedStatusMsg;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.contacts.Contact;

public class FlickrNetworkLoader implements IAsyncNetworkLoader {

	/**
	 * Friends to load in first degree network
	 */
	private static final int MAX_FRIENDS_PER_PERSON_TO_LOAD_1 = 50;

	/**
	 * Friends to load in 2nd degree network
	 */
	private static final int MAX_FRIENDS_PER_PERSON_TO_LOAD_2 = 0;

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
	private synchronized void loadRecursive(String contactId, int depth, int maxDepth,
			INetworkListener networkListener) throws IOException, SAXException, FlickrException {

		depth++;

		if (depth > maxDepth) {
			return;
		}

		if (isClosed) {
			return;
		}

		Collection<Contact> contacts = flickrAPI.getContactsInterface().getPublicList(contactId);

		int maxToLoad;
		if (depth > 1) {
			maxToLoad = MAX_FRIENDS_PER_PERSON_TO_LOAD_2;
		} else {
			maxToLoad = MAX_FRIENDS_PER_PERSON_TO_LOAD_1;
		}

		int count = 0;
		boolean create = true;
		for (Contact contact : contacts) {

			if (++count > maxToLoad) {
				create = false;
			}

			networkListener.acceptNewConnection(contactId, contact.getId(), depth, create);
		}

		count = 0;
		for (Contact contact : contacts) {
			if (++count > maxToLoad) {
				break;
			}
			loadRecursive(contact.getId(), depth, maxDepth, networkListener);
		}
	}

	public void loadNetworkAsync(Person userRoot, int degrees, INetworkListener networkListener) {
		(new NetworkLoaderThread(userRoot, degrees, networkListener)).start();

	}

	class NetworkLoaderThread extends Thread {
		private INetworkListener networkListener;
		private int degrees;
		private Person userRoot;

		public NetworkLoaderThread(Person userRoot, int degrees, INetworkListener networkListener) {
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
				e.getTargetException().printStackTrace();
			}
		}

		public void loadNetwork() throws InterruptedException, InvocationTargetException {
			TrackedStatusMsg statusMsg = new TrackedStatusMsg("Loading " + userRoot + "'s friends");

			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					userRoot.setAnchored(true);
				}
			});

			try {
				loadRecursive(userRoot.getId(), 0, degrees, networkListener);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// SwingUtilities.invokeAndWait(new Runnable() {
			// public void run() {
			// userRoot.setAnchored(false);
			// }
			// });
			statusMsg.finished();
		}
	}

	private boolean isClosed = false;

	public void close() {
		isClosed = true;
	}

}
