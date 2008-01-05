package ca.shu.ui.chameleon.adapters.flickr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.adapters.IPhotoSourceException;
import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.SourceEmptyException;
import ca.shu.ui.chameleon.objects.IStreamingPhotoHolder;
import ca.shu.ui.lib.util.Util;

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.Size;

/*
 * 
 * TODO: A abstract "Redo" function when a connection fails.
 * 
 */
public abstract class FlickrPhotoSource implements IStreamingPhotoSource {

	private static int BUFFER_SIZE = 4;

	public static int DEFAULT_PHOTO_SIZE = Size.SMALL;

	public static FlickrPhotoSource createInterestingSource() {
		return new InterestingSource();
	}

	public static FlickrPhotoSource createRecentSource() {
		return new RecentSource();
	}

	public static FlickrPhotoSource createTestSource() {
		return new TestRetriever();
	}

	public static FlickrPhotoSource createUserSource(String userName) {
		return createUserSource(userName, false);
	}

	public static FlickrPhotoSource createUserSource(String handle,
			boolean isUserId) {
		return new UserSource(handle, isUserId);
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

	private CrawlerState state;

	private boolean isRetrieverAlive = true;

	private Vector<FlickrPhoto> photos;

	private PicsRetriever retriever = null;

	protected FlickrPhotoSource() {
		super();

		photos = new Vector<FlickrPhoto>();
		retriever = new PicsRetriever();
		retriever.start();
	}

	protected abstract PhotoList getPhotoList() throws FlickrException,
			IOException, SAXException, SourceEmptyException;

	protected void initSource() throws IOException, SAXException,
			FlickrException {

	}

	public void close() {
		isRetrieverAlive = false;

		synchronized (photos) {
			photos.notifyAll();
		}
	}

	public File getCachedFolder() {
		File cachedFolder = new File(FlickrAPI.FLICKR_CACHE_FOLDER_NAME + "/"
				+ getCrawlerType());

		if (!cachedFolder.exists()) {
			cachedFolder.mkdirs();
		}

		return cachedFolder;
	}

	protected abstract String getCrawlerType();

	public CrawlerState getCrawlerState() {
		return state;
	}

	public IPhoto getPhoto() throws IPhotoSourceException, SourceEmptyException {
		Collection<IPhoto> photos = getPhotos(1);
		return photos.iterator().next();
	}

	public Collection<IPhoto> getPhotos(int count) throws IPhotoSourceException {
		synchronized (photos) {
			photos.notifyAll(); // notify that photos may have been depleted

			if (photos.size() >= count) {
				Vector<IPhoto> photosToReturn = new Vector<IPhoto>(count);

				for (int i = 0; i < count; i++) {
					photosToReturn.add(photos.remove(0));
				}
				return photosToReturn;
			}
			// retriever has stopped, throw error
			else if (!retriever.isAlive()) {
				throw new IPhotoSourceException("Source Error");
			}

			return new Vector<IPhoto>(0);
		}
	}

	public Thread getPhotosAsync(int count, IStreamingPhotoHolder photoHolder) {
		Thread thread = new AsyncPhotoAdder(photoHolder, count);

		thread.start();

		return thread;
	}

	public abstract CrawlerState initCrawlerState();

	static final String CRAWLER_STATE_FILE_NAME = "crawlerState.data";

	private void loadCrawlerState() {

		FileInputStream f_in;

		try {
			File file = new File(getCachedFolder(), CRAWLER_STATE_FILE_NAME);

			f_in = new FileInputStream(file);

			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			Object obj;

			obj = obj_in.readObject();

			if (obj instanceof CrawlerState) {
				state = (CrawlerState) obj;
				return;
			} else {
				System.out
						.println("Stored crawler state was invalid. it was of type"
								+ obj.getClass());
			}

		} catch (FileNotFoundException e) {
			System.out.println("No saved crawler state found at: "
					+ getCachedFolder().toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param id
	 * @return FlickrPhoto, null if cached copy could not be found
	 */
	private FlickrPhoto loadMetadata(String id) {
		File file = new File(getCachedFolder(), id + ".data");

		if (file.exists()) {

			Object obj = loadObject(file.toString());

			if (obj != null && obj instanceof FlickrPhoto) {
				return (FlickrPhoto) obj;
			}
		}
		return null;

	}

	private void saveCrawlerState() {
		// Write to disk with FileOutputStream
		FileOutputStream f_out;
		try {
			File file = new File(getCachedFolder(), CRAWLER_STATE_FILE_NAME);

			f_out = new FileOutputStream(file);
			ObjectOutputStream obj_out;
			obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(state);

			System.out.println("Saved: " + state.getHandle() + ": "
					+ state.currentDate + ", page: " + (state.getCurrentPage())
					+ ", count: " + state.getCurrentPhotoCounter());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveMetadata(FlickrPhoto photo) {
		// Write to disk with FileOutputStream
		FileOutputStream f_out;
		try {

			File file = new File(getCachedFolder(), photo.getId() + ".data");

			if (file.exists()) {
				Util.debugMsg("Updating metadata file cache");
			} else {
				Util.debugMsg("Writing metadata file to cache");
			}

			f_out = new FileOutputStream(file);

			ObjectOutputStream obj_out;
			obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(photo);

			saveCrawlerState();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class AsyncPhotoAdder extends Thread {
		int numOfPhotosToAdd;

		IStreamingPhotoHolder photoHolder;

		public AsyncPhotoAdder(IStreamingPhotoHolder photoHolder,
				int numOfPhotosToAdd) {
			super();
			this.numOfPhotosToAdd = numOfPhotosToAdd;
			this.photoHolder = photoHolder;
		}

		@Override
		public synchronized void run() {

			while (true && isRetrieverAlive) {
				try {
					synchronized (photos) {
						while ((numOfPhotosToAdd > 0) && (photos.size() > 0)) {
							IPhoto photoToAdd = photos.remove(0);

							// keep trying to add the photo until its full
							while (true) {
								if (photoHolder.addPhoto(photoToAdd)) {
									numOfPhotosToAdd--;
								} else {
									Thread.sleep(3000);
								}
							}
						}

						// if > 0 then still need more photos, notify the
						// threads which are adding photos
						if (numOfPhotosToAdd > 0) {
							photos.notifyAll();
						}

						photos.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

	}

	class PicsRetriever extends Thread {
		int errorCount = 0;

		public PicsRetriever() {
			super("Flickr Picture Retriever");
		}

		@SuppressWarnings("unchecked")
		protected void pictureRetrieveLoop() throws FlickrException,
				IOException, SAXException {

			initSource();

			if (state == null) {
				state = initCrawlerState();
			}

			while (true && isRetrieverAlive) {

				PhotoList list = null;

				try {
					list = getPhotoList();
				} catch (SourceEmptyException e1) {
					System.out.println("Source has run out of Photos");
					break;
				}

				if (list != null) {

					for (int i = state.getCurrentPhotoCounter(); i < list
							.size(); i++) {
						if (!isRetrieverAlive)
							return;
						try {
							state.setCurrentPhotoCounter(i);

							Photo photo = (Photo) list.get(i);

							// Wrap the photos in a proxy object which makes
							// additional calls for more information
							FlickrPhoto photoWrapper = new FlickrPhoto(photo
									.getId());

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
	static final int GET_PER_BATCH = 50;

	@Override
	protected PhotoList getPhotoList() throws FlickrException, IOException,
			SAXException, SourceEmptyException {

		CrawlerState state = getCrawlerState();

		PhotoList list = null;
		try {

			list = FlickrAPI.getInterfaces().getInterestingnessInterface()
					.getList(state.currentDate, Extras.ALL_EXTRAS,
							GET_PER_BATCH, state.currentPage);
		} catch (FlickrException e) {
			// no pictures available for that date
			System.out.println(e + " , " + state.currentDate);

		}

		if ((list == null) || (list.size() == 0)) {
			// goto previous day
			state.currentDate = new Date(state.currentDate.getTime()
					- (3600 * 1000 * 24));
			state.currentPage = 1;

		} else {
			System.out.println("Got Interesting Pics for: " + state.currentDate
					+ " page: " + state.currentPage);

		}

		return list;
	}

	@Override
	public String getCrawlerType() {
		return "IntPhotos";
	}

	@Override
	public CrawlerState initCrawlerState() {
		System.out.println("reseting state" + (new Date()));
		return new CrawlerState(getCrawlerType(), new Date(), 1, 0,
				GET_PER_BATCH);
	}

}

class RecentSource extends FlickrPhotoSource {
	static final int GET_PER_BATCH = 50;

	@Override
	protected PhotoList getPhotoList() throws FlickrException, IOException,
			SAXException, SourceEmptyException {

		CrawlerState state = getCrawlerState();

		PhotoList list = null;
		try {
			list = FlickrAPI.getInterfaces().getPhotosInterface().getRecent(
					state.getNumPerPage(), state.getCurrentPage());

		} catch (FlickrException e) {
			// no pictures available for that date
			System.out.println(e + " , " + state.currentDate);
			throw new SourceEmptyException("No more 'recent' photos");
		}

		return list;
	}

	@Override
	public String getCrawlerType() {
		return "RecentPhotos";
	}

	@Override
	public CrawlerState initCrawlerState() {
		System.out.println("reseting state" + (new Date()));
		return new CrawlerState(getCrawlerType(), new Date(), 1, 0,
				GET_PER_BATCH);
	}

}

class TestRetriever extends FlickrPhotoSource {

	@Override
	protected PhotoList getPhotoList() throws FlickrException, IOException,
			SAXException, SourceEmptyException {
		throw new SourceEmptyException("Empty");
	}

	@Override
	public String getCrawlerType() {
		return "Test";
	}

	@Override
	public CrawlerState initCrawlerState() {
		return new CrawlerState("Test", new Date(), 1, 1, 50);
	}

}

class UserSource extends FlickrPhotoSource {
	static final int GET_PER_BATCH = 50;

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
	protected PhotoList getPhotoList() throws FlickrException, IOException,
			SourceEmptyException, SAXException {
		System.out.println("Started: Getting " + handle + "'s Pictures");
		PhotoList list = FlickrAPI.getInterfaces().getPeopleInterface()
				.getPublicPhotos(userId, GET_PER_BATCH, pageNum++);
		if (list.size() == 0) {
			throw new SourceEmptyException("no more from user photos");
		}

		System.out.println("Completed: Getting " + handle + "'s Pictures");
		return list;
	}

	@Override
	protected void initSource() throws IOException, SAXException,
			FlickrException {
		if (isUserId) {
			userId = handle;
		} else {
			userId = FlickrAPI.getInterfaces().getPeopleInterface()
					.findByUsername(handle).getId();
		}

	}

	@Override
	public String getCrawlerType() {
		return "UserPhotos/" + userId;
	}

	@Override
	public CrawlerState initCrawlerState() {
		return new CrawlerState(getCrawlerType(), new Date(), 1, 0,
				GET_PER_BATCH);
	}
}