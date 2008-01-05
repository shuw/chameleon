package ca.shu.ui.chameleon.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.adapters.IPhotoSourceException;
import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.SourceEmptyException;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.objects.Border;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

/*
 * Wraps the collage and its interface
 */
public class PhotoCollage extends ModelObject implements IStreamingPhotoHolder {

	private static final long serialVersionUID = 1L;

	private boolean autoScrollEnabled = true;

	private Thread autoScrollThread = null;

	private Collage collage;

	private IStreamingPhotoSource photoSource;

	private int scrollIntervalMs = 1000;

	public PhotoCollage(IStreamingPhotoSource photoSource) {
		this(photoSource, 1200, 1200);
	}

	public PhotoCollage(IStreamingPhotoSource photoSource, double collageWidth,
			double collageHeight) {
		super(photoSource);

		this.photoSource = photoSource;

		collage = new Collage();
		collage.setBounds(0, 0, collageWidth, collageHeight);
		addChild(collage);

		addChild(new Border(this, Style.COLOR_FOREGROUND));

		setBounds(getFullBounds());

		setAutoScroll(true);
	}

	public boolean addPhoto(IPhoto photo) {
		return collage.addPhoto(photo);

	}

	public void decreaseScrollSpeed() {
		scrollIntervalMs += 400;
	}

	public void getMorePhotos(int count) {
		photoSource.getPhotosAsync(count, this);
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		/*
		 * TODO: Create menu
		 */
		// addButton(new GButton("+Width", new Runnable() {
		// public void run() {
		// collage.setWidthAndUpdateCollage(collage.getWidth() + 500);
		//
		// }
		// }));
		// addButton(new GButton("-Width", new Runnable() {
		// public void run() {
		// collage.setWidthAndUpdateCollage(collage.getWidth() - 500);
		//
		// }
		// }));
		//
		// addButton(new GButton("+Scroll Speed", new Runnable() {
		// public void run() {
		// collage.increaseScrollSpeed();
		//
		// }
		// }));
		// addButton(new GButton("-Scroll Speed", new Runnable() {
		// public void run() {
		// collage.decreaseScrollSpeed();
		//
		// }
		// }));
		//
		// // addButton(new GButton("Get next " + getPhotosNum + " Photos",
		// // new Runnable() {
		// // public void run() {
		// //
		// // collage.getMorePhotos(getPhotosNum);
		// // }
		// // }));
		//
		// autoScrollBtn = (TextButton) addButton(new GButton(
		// getAutoScrollBtnText(), new Runnable() {
		// public void run() {
		//
		// if (collage.isAutoScrollEnabled()) {
		// collage.setAutoScroll(false);
		//
		// } else {
		// collage.setAutoScroll(true);
		// }
		// autoScrollBtn.setText(getAutoScrollBtnText());
		// }
		// }));
		super.constructMenu(menu);
	}

	public void increaseScrollSpeed() {
		scrollIntervalMs -= 400;

		if (scrollIntervalMs <= 0) {
			scrollIntervalMs = 0;
		}
	}

	public boolean isAutoScrollEnabled() {
		return autoScrollEnabled;
	}

	@Override
	public void prepareForDestroy() {
		photoSource.close();
		super.prepareForDestroy();
	}

	public void removePhoto(GPhoto photo) {
		collage.removePhoto(photo);
	}

	public void setAutoScroll(boolean bool) {
		this.autoScrollEnabled = bool;

		if (autoScrollEnabled) {
			if ((autoScrollThread == null) || !autoScrollThread.isAlive()) {
				autoScrollThread = new AutoScrollThread();
				autoScrollThread.start();
			}
		}

		// autoScrollBtn.setText("NadaHSHSHS");
	}

	public void setSourceState(SourceState state) {
		if (state == SourceState.ERROR) {
			System.out.println("Source Error Encountered");
			UserMessages.showError("Error loading photos in " + getName());
		}

	}

	public void setWidthAndUpdateCollage(double width) {
		collage.doCollageLayout(width);
	}

	class AutoScrollThread extends Thread {
		public AutoScrollThread() {
			super("Auto Scroller");
		}

		@Override
		public void run() {
			while (autoScrollEnabled && !PhotoCollage.this.isDestroyed()) {

				try {
					Collection<IPhoto> photos;

					photos = photoSource.getPhotos(1);

					if (photos.size() > 0) {
						addPhoto(photos.iterator().next());
					}
				} catch (IPhotoSourceException e1) {
					setSourceState(SourceState.ERROR);
					break;
				} catch (SourceEmptyException e) {
					System.out.println("Source empty, turning autoscroll off");
					break;
				}

				try {
					Thread.sleep(scrollIntervalMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setAutoScroll(false);
		}
	}

	@Override
	public String getTypeName() {
		return "Photo Collage";
	}
}

class Collage extends WorldObject {

	private static final long serialVersionUID = 1L;

	private static final int ADD_PHOTO_TIME_MS = 800;

	private Object animationLock = new Object();

	private CollageLayoutThread collageLayoutThread = null;

	private Stack<GPhoto> photoQueue;

	public Collage() {
		super();
		this.setBounds(0, 0, 1500, 1500);
		photoQueue = new Stack<GPhoto>();

		// Insert Photo Thread is responsible for adding new photos from the
		// stream
		(new InsertPhotoThread()).start();

	}

	private void movePhotosBy(Collection<GPhoto> photos, double moveBy) {
		for (GPhoto photo : photos) {
			if (!photo.isSelected()) {
				double moveTo = photo.getOffset().getY() - moveBy;

				photo.animateToPositionScaleRotation(photo.getOffset().getX(),
						moveTo, 1, 0, 1000);

				// its moved off the screen
				if (moveTo < 0) {
					addActivity(new Fader(photo, 1000, 0f));
					photo
							.animateToPositionScaleRotation(photo.getOffset()
									.getX(), photo.getOffset().getY() - 500, 1,
									0, 1000);
					addActivity(new RemovePhoto(1000, photo));
				}
			}
		}
	}

	private Collection<GPhoto> getChildrenPhotos() {
		Collection<GPhoto> photos = new ArrayList<GPhoto>(getChildrenCount());

		Iterator<?> it = getChildrenIterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof GPhoto) {
				photos.add((GPhoto) obj);
			}
		}
		return photos;
	}

	/*
	 * Inserts photos and lays them out in the correct position
	 */
	protected void layoutPhotos(Collection<GPhoto> photosToInsert,
			boolean disableScroll, double insertX, double insertY) {

		double rowHeight = 0; // max Y boundary
		double minYBound = Double.MAX_VALUE; // min Y boundary

		Util.Assert(!(disableScroll && photosToInsert.size() > 0),
				"Inserting while scrolling is disabled");

		if (!disableScroll) {
			/*
			 * Find insert position
			 */
			Collection<GPhoto> collagePhotos = getChildrenPhotos();

			for (GPhoto photo : collagePhotos) {
				double photoX = photo.getOffset().getX() + photo.getWidth();
				double photoY = photo.getOffset().getY();

				if (photoY > insertY) {
					insertY = photoY;
					insertX = photoX;
				} else if (photoY >= insertY) {
					if (photoX > insertX) {
						insertX = photoX;
					}
				}

				if (photo.getHeight() > rowHeight) {
					rowHeight = photo.getHeight();
				}
				if (photoY < minYBound) {
					minYBound = photoY;
				}
			}

			/*
			 * Insert Photos
			 */
			for (GPhoto photo : photosToInsert) {
				// if width exceeded, goto new row at the bottom
				if (insertX + photo.getWidth() > this.getWidth()) {
					insertY += rowHeight;
					insertX = 0;
				}

				this.addChild(photo);

				if (disableScroll) {
					photo.animateToPosition(insertX, insertY, 1000);
				} else {
					photo.setOffset(insertX, insertY);
					// photo.setTransparency(0);
					addActivity(new Fader(photo, ADD_PHOTO_TIME_MS, 1f));
				}

				insertX += photo.getWidth();

				if (photo.getHeight() > rowHeight) {
					rowHeight = photo.getHeight();
				}
			}

			/*
			 * Scroll photos
			 */
			if (rowHeight + insertY > this.getHeight()) {
				double moveBy = rowHeight;

				// minYBound -= 10;
				if (minYBound > moveBy) {
					moveBy = minYBound;
				}

				double moveByOrg = moveBy;

				for (GPhoto photo : collagePhotos) {
					if ((photo.getOffset().getY() - moveByOrg) < 0) {
						double newMoveBy = photo.getOffset().getY()
								+ photo.getHeight();

						if (newMoveBy > moveBy) {
							moveBy = newMoveBy;
						}
					}
				}

				movePhotosBy(collagePhotos, moveBy);
				movePhotosBy(photosToInsert, moveBy);
			}
		}
		// doCollageLayout(getCollageWidth(), true);

	}

	public void addAndScrollPhotos(Collection<GPhoto> photos) {
		layoutPhotos(photos, false, 0, 0);
	}

	public boolean addPhoto(IPhoto p) {
		synchronized (photoQueue) {
			if (photoQueue.size() > 10) {
				return false;
			}

			// create the GPhoto object and wait for the image to load
			GPhoto photo = new GPhoto(p);
			synchronized (photo) {
				try {
					while (!photo.isImageLoaded()) {
						photo.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (photo == null) {
				System.out.println("Could not create GPhoto");
			}

			boolean rtnValue = photoQueue.add(photo);
			photoQueue.notifyAll();

			return rtnValue;
		}
	}

	public void cleanUpLayout() {
		cleanUpLayout(this.getWidth());
	}

	public void cleanUpLayout(double newWidth) {
		Collection<GPhoto> collagePhotos = getChildrenPhotos();
		setWidth(newWidth);

		layoutPhotos(collagePhotos, true, 0, 0);

		if (newWidth != getParent().getWidth()) {
			getParent().animateToBounds(getParent().getX(), getParent().getY(),
					newWidth, getParent().getHeight(), 1000);
		}
	}

	public void doCollageLayout(double newWidth) {
		if (collageLayoutThread == null) {
			collageLayoutThread = new CollageLayoutThread(newWidth);
			collageLayoutThread.start();
		}
	}

	@Override
	protected void prepareForDestroy() {
		synchronized (photoQueue) {
			// wake up Insert Photo Thread so it can be destroyed
			photoQueue.notifyAll();
		}
		super.prepareForDestroy();
	}

	public void removePhoto(GPhoto photo) {
		this.removeChild(photo);
	}

	class CollageLayoutThread extends Thread {
		double newWidth;

		public CollageLayoutThread(double newWidth) {
			super();
			this.newWidth = newWidth;
		}

		@Override
		public void run() {
			synchronized (animationLock) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Collage.this.cleanUpLayout(newWidth);

					}
				});

				try {
					Thread.sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			collageLayoutThread = null;
		}
	}

	class InsertPhotoThread extends Thread {
		public InsertPhotoThread() {
			super("Inserting Photo");
		}

		@Override
		@SuppressWarnings("unchecked")
		public void run() {
			while (!Collage.this.isDestroyed()) {
				try {
					synchronized (photoQueue) {
						if (photoQueue.size() > 0) {
							synchronized (animationLock) {

								addAndScrollPhotos((Stack<GPhoto>) (photoQueue
										.clone()));
								photoQueue.clear();

								Thread.sleep(1200);
							}
						}
						photoQueue.wait();

					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class RemovePhoto extends PActivity {
		PNode photoToRemove;

		public RemovePhoto(long aDuration, PNode photoToRemove) {
			super(aDuration);
			this.photoToRemove = photoToRemove;
		}

		@Override
		protected void activityFinished() {
			// TODO Auto-generated method stub
			super.activityFinished();
			if (photoToRemove.getParent() != null) {
				photoToRemove.getParent().removeChild(photoToRemove);
			}
		}

	}
}
