package ca.shu.ui.chameleon.flickr.adapters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.IStreamingSourceException;
import ca.shu.ui.chameleon.adapters.SourceEmptyException;
import ca.shu.ui.chameleon.flickr.FlickrAPI;
import ca.shu.ui.lib.util.Util;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.photos.Size;

/*
 * 
 * TODO: A abstract "Redo" function when a connection fails.
 * 
 */
public abstract class FlickrPhotoSource implements IStreamingPhotoSource {

	private static int BUFFER_SIZE = 4;
	public static int DEFAULT_PHOTO_SIZE = Size.SMALL;
	static final String CRAWLER_STATE_FILE_NAME = "crawlerState.data";

	static final int GET_PER_BATCH = 50;

	public static FlickrPhotoSource createInterestingSource() {
		FlickrPhotoSource source = new InterestingSource();
		source.start();
		return source;
	}

	public static FlickrPhotoSource createRecentSource() {
		FlickrPhotoSource source = new RecentSource();
		source.start();
		return source;
	}

	public static FlickrPhotoSource createSearchSource(String searchTerm) {
		FlickrPhotoSource source = new SearchSource(searchTerm);
		source.start();
		return source;
	}

	public static FlickrPhotoSource createTestSource() {
		FlickrPhotoSource source = new TestRetriever();
		source.start();
		return source;
	}

	public static FlickrPhotoSource createUserSource(String userName) {
		return createUserSource(userName, false);
	}

	public static FlickrPhotoSource createUserSource(String handle, boolean isUserId) {
		FlickrPhotoSource source = new UserSource(handle, isUserId);
		source.start();
		return source;
	}

	public static Object loadObject(String fileName) {

		FileInputStream f_in;

		try {
			f_in = new FileInputStream(fileName);

			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			Object obj;

			obj = obj_in.readObject();

			return obj;
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Flickr flickrAPI;

	private boolean isRetrieverAlive = true;

	private Vector<FlickrPhoto> photos;

	private FlickPhotoRetriever retriever = null;

	private CrawlerState state;

	protected FlickrPhotoSource() {
		super();
		flickrAPI = FlickrAPI.create();

		photos = new Vector<FlickrPhoto>();

	}

	public void start() {
		retriever = new FlickPhotoRetriever();
		retriever.start();
	}

	protected Flickr getAPI() {
		return flickrAPI;
	}

	protected abstract PhotoList getPhotoList() throws IOException, SAXException,
			SourceEmptyException;

	protected void initSource() throws IOException, SAXException, FlickrException {

	}

	public void close() {
		isRetrieverAlive = false;

		synchronized (photos) {
			photos.notifyAll();
		}
	}

	public FlickrPhoto getPhoto() throws IStreamingSourceException, SourceEmptyException {
		Collection<FlickrPhoto> photos = getPhotos(1);
		return photos.iterator().next();
	}

	public Collection<FlickrPhoto> getPhotos(int count) throws IStreamingSourceException {
		synchronized (photos) {
			// retriever has stopped, throw error
			if (!retriever.isAlive()) {
				throw new IStreamingSourceException("Source closed");
			}

			photos.notifyAll(); // photos will change
			Vector<FlickrPhoto> photosToReturn = new Vector<FlickrPhoto>(count);
			for (int i = 0; i < count; i++) {
				try {
					while (photos.size() == 0) {
						photos.wait();
					}
					photosToReturn.add(photos.remove(0));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return photosToReturn;
		}
	}

	public CrawlerState getState() {
		if (state == null) {
			state = initState();
		}
		return state;
	}

	public CrawlerState initState() {
		return new CrawlerState(getName(), new Date(), 1, 0, GET_PER_BATCH);
	}

	class FlickPhotoRetriever extends Thread {
		int errorCount = 0;

		public FlickPhotoRetriever() {
			super("Flickr Picture Retriever");
		}

		@SuppressWarnings("unchecked")
		protected void pictureRetrieveLoop() throws FlickrException, IOException, SAXException {

			initSource();

			if (state == null) {
				state = initState();
			}

			while (true && isRetrieverAlive) {

				PhotoList list = null;

				try {
					list = getPhotoList();
				} catch (SourceEmptyException e1) {
					Util.debugMsg("Souce empty: " + e1.getMessage());
					break;
				}

				if (list != null) {

					for (int i = state.getCurrentPhotoCounter(); i < list.size(); i++) {
						if (!isRetrieverAlive)
							return;
						try {
							state.setCurrentPhotoCounter(i);

							Photo photo = (Photo) list.get(i);

							// Wrap the photos in a proxy object which makes
							// additional calls for more information
							FlickrPhoto photoWrapper = new FlickrPhoto(photo.getId());

							// Wait to get more photos
							synchronized (photos) {
								photos.add(photoWrapper);
								photos.notifyAll();

								try {
									if (photos.size() > BUFFER_SIZE) {
										// Keep an extra 4 photos in the buffer
										photos.wait();
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

							}
						} catch (Exception e) {
							errorCount++;
							e.printStackTrace();

						} finally {
							if (errorCount >= 40) {
								System.out.println("Error count exceeded 40");
								return;
							}
						}
					}

					// goto the next page
					if (state.currentPhotoCounter >= (list.size() - 1)) {
						state.currentPhotoCounter = 0;
						state.currentPage++;
					}
				}
			}
		}

		@Override
		public void run() {
			try {
				pictureRetrieveLoop();
			} catch (FlickrException e) {
				System.out.println(e);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
	}
}

class InterestingSource extends FlickrPhotoSource {
	private boolean getMostCurrentPhotos = true;

	@Override
	protected synchronized PhotoList getPhotoList() throws IOException, SAXException,
			SourceEmptyException {

		CrawlerState state = getState();

		PhotoList list = null;
		try {
			if (getMostCurrentPhotos) {
				getMostCurrentPhotos = false;
				list = getAPI().getInterestingnessInterface().getList((Date) null,
						Extras.ALL_EXTRAS, GET_PER_BATCH, state.currentPage);
			} else {
				list = getAPI().getInterestingnessInterface().getList(state.currentDate,
						Extras.ALL_EXTRAS, GET_PER_BATCH, state.currentPage);
			}
		} catch (FlickrException e) {
			// no pictures available for that date
			System.out.println(e + " , " + state.currentDate);

		}

		if ((list == null) || (list.size() == 0)) {
			// goto previous day
			state.currentDate = new Date(state.currentDate.getTime() - (3600 * 1000 * 24));
			state.currentPage = 1;

		} else {
			System.out.println("Got Interesting Pics for: " + state.currentDate + " page: "
					+ state.currentPage);

		}

		return list;
	}

	public String getName() {
		return "Interesting Photos";
	}

}

class RecentSource extends FlickrPhotoSource {

	@Override
	protected synchronized PhotoList getPhotoList() throws IOException, SAXException,
			SourceEmptyException {

		CrawlerState state = getState();

		PhotoList list = null;
		try {
			list = getAPI().getPhotosInterface().getRecent(state.getNumPerPage(),
					state.getCurrentPage());

		} catch (FlickrException e) {
			// no pictures available for that date
			throw new SourceEmptyException(e.getMessage());
		}

		return list;
	}

	public String getName() {
		return "Recent Photos";
	}

}

class SearchSource extends FlickrPhotoSource {
	private final String searchterm;
	private final SearchParameters searchParam;

	public SearchSource(String searchterm) {
		super();
		this.searchterm = searchterm;
		searchParam = new SearchParameters();
		searchParam.setSort(SearchParameters.RELEVANCE);
		searchParam.setText(searchterm);

	}

	@Override
	protected PhotoList getPhotoList() throws IOException, SAXException, SourceEmptyException {
		CrawlerState state = getState();

		try {
			System.out.println("searchParam: " + searchParam);
			return getAPI().getPhotosInterface().search(searchParam, state.getNumPerPage(),
					state.getCurrentPage());
		} catch (FlickrException e) {
			throw new SourceEmptyException(e.getMessage());
		}
	}

	public String getName() {
		return "Search : " + searchterm;
	}

}

class TestRetriever extends FlickrPhotoSource {

	@Override
	protected PhotoList getPhotoList() throws IOException, SAXException, SourceEmptyException {
		throw new SourceEmptyException("Empty");
	}

	public String getName() {
		return "Test";
	}

}

class UserSource extends FlickrPhotoSource {

	String handle;

	boolean isUserId;

	int pageNum = 0;

	CrawlerState state;

	String userId;

	public UserSource(String handle, boolean isUserId) {
		super();
		this.handle = handle;
		this.isUserId = isUserId;

	}

	// @Override
	// public void loadCrawlerState() {
	// //do nothing
	// }

	@Override
	protected synchronized PhotoList getPhotoList() throws IOException, SourceEmptyException,
			SAXException {
		System.out.println("Started: Getting " + handle + "'s Pictures");
		PhotoList list;
		try {
			list = getAPI().getPeopleInterface().getPublicPhotos(userId, GET_PER_BATCH, pageNum++);
		} catch (FlickrException e) {
			throw new SourceEmptyException(e.getMessage());
		}

		System.out.println("Completed: Getting " + handle + "'s Pictures");
		return list;
	}

	@Override
	protected synchronized void initSource() throws IOException, SAXException, FlickrException {
		if (isUserId) {
			userId = handle;
		} else {
			userId = getAPI().getPeopleInterface().findByUsername(handle).getId();
		}

	}

	public String getName() {
		return "User Photos";
	}

}