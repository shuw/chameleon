package ca.shu.ui.chameleon.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.adapters.IPhotoSourceException;
import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.SourceEmptyException;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.objects.Border;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * Photo Collage
 * 
 * @author Shu Wu
 */
public class PhotoCollage extends ModelObject implements IStreamingPhotoHolder {

	private static final int MAXIMUM_WIDTH = 2500;

	private static final int MINIMUM_WIDTH = 200;

	private static final long serialVersionUID = 1L;

	private static final int WIDTH_INTERVAL = 400;

	private boolean autoScrollEnabled = true;

	private Thread autoScrollThread = null;

	private Collage collage;

	private IStreamingPhotoSource photoSource;

	private int scrollDelayMs = 1000;

	public PhotoCollage(IStreamingPhotoSource photoSource) {
		this(photoSource, 1200, 1200);
	}

	public PhotoCollage(IStreamingPhotoSource photoSource, double collageWidth,
			double collageHeight) {
		super(photoSource);
		setPaint(Style.COLOR_BACKGROUND);

		this.photoSource = photoSource;

		collage = new Collage();
		collage.setBounds(0, 0, collageWidth, collageHeight);
		addChild(collage);

		addChild(new Border(this, Style.COLOR_FOREGROUND));

		setBounds(getFullBounds());

		setAutoScroll(true);
	}

	private boolean canChangeScrollSpeed(boolean positive) {
		return true;
	}

	private boolean canChangeSize(boolean positive) {
		if (positive) {
			if (getWidth() + WIDTH_INTERVAL < MAXIMUM_WIDTH) {
				return true;
			}

		} else {
			if (getWidth() - WIDTH_INTERVAL > MINIMUM_WIDTH) {
				return true;
			}
		}
		return false;

	}

	private void changeScrollSpeed(boolean positive) {
		if (positive) {
			scrollDelayMs -= 400;

			if (scrollDelayMs <= 0) {
				scrollDelayMs = 0;
			}
		} else {
			scrollDelayMs += 400;
		}

	}

	private void changeSize(boolean positive) {
		if (canChangeSize(positive)) {
			if (positive) {
				setCollageWidth(getWidth() + WIDTH_INTERVAL);
			} else {
				setCollageWidth(getWidth() - WIDTH_INTERVAL);
			}
		}
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);
		ChameleonMenus.constructMenu(this, menu);

		if (canChangeSize(true)) {
			menu.addAction(new ChangeSizeAction("+ Width", true));
		}

		if (canChangeSize(false)) {
			menu.addAction(new ChangeSizeAction("- Width", false));
		}

		if (isAutoScrollEnabled()) {

			if (canChangeScrollSpeed(true)) {
				menu.addAction(new ChangeScrollSpeedAction("+ Scroll Speed",
						true));
			}
			if (canChangeScrollSpeed(false)) {
				menu.addAction(new ChangeScrollSpeedAction("- Scroll Speed",
						false));
			}

			menu.addAction(new SetAutoScrollAction("Stop scrolling", false));
		} else {
			menu.addAction(new SetAutoScrollAction("Stop scrolling", true));
		}

		// addButton(new GButton("Get next " + getPhotosNum + " Photos",
		// new Runnable() {
		// public void run() {
		//
		// collage.getMorePhotos(getPhotosNum);
		// }
		// }));

	}

	public boolean addPhoto(IPhoto photo) {
		return collage.addPhoto(photo);

	}

	public void getMorePhotos(int count) {
		photoSource.getPhotosAsync(count, this);
	}

	@Override
	public String getTypeName() {
		return "Photo Collage";
	}

	public boolean isAutoScrollEnabled() {
		return autoScrollEnabled;
	}

	@Override
	public void prepareForDestroy() {
		photoSource.close();
		super.prepareForDestroy();
	}

	public void removePhoto(Photo photo) {
		collage.removePhoto(photo);
	}

	public void setAutoScroll(boolean enabled) {
		this.autoScrollEnabled = enabled;

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

	public void setCollageWidth(double width) {
		Util.Assert(width >= MINIMUM_WIDTH && width <= MAXIMUM_WIDTH,
				"Incorrect size");

		collage.setWidth(width);

		if (width != getParent().getWidth()) {
			animateToBounds(getX(), getY(), width, getHeight(), 1000);
		}
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
					Thread.sleep(scrollDelayMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setAutoScroll(false);
		}
	}

	class ChangeScrollSpeedAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		private boolean positive;

		public ChangeScrollSpeedAction(String description, boolean positive) {
			super(description);
			this.positive = positive;
		}

		@Override
		protected void action() throws ActionException {
			changeScrollSpeed(positive);
		}

	}

	class ChangeSizeAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		private boolean positive;

		public ChangeSizeAction(String description, boolean positive) {
			super(description);
			this.positive = positive;
		}

		@Override
		protected void action() throws ActionException {
			changeSize(positive);
		}
	}

	class SetAutoScrollAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		private boolean enabled;

		public SetAutoScrollAction(String description, boolean enabled) {
			super(description);
			this.enabled = enabled;
		}

		@Override
		protected void action() throws ActionException {
			setAutoScroll(enabled);
		}

	}
}

class Collage extends WorldObject {

	private static final int ADD_PHOTO_TIME_MS = 800;

	private static final long serialVersionUID = 1L;

	private Object animationLock = new Object();

	private Stack<Photo> photoQueue;

	public Collage() {
		super();
		this.setBounds(0, 0, 1500, 1500);
		photoQueue = new Stack<Photo>();
		this.setPickable(false);

		// Insert Photo Thread is responsible for adding new photos from the
		// stream
		(new InsertPhotoThread()).start();

	}

	private Collection<Photo> getChildrenPhotos() {
		Collection<Photo> photos = new ArrayList<Photo>(getChildrenCount());

		Iterator<?> it = getChildrenIterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof Photo) {
				photos.add((Photo) obj);
			}
		}
		return photos;
	}

	private void movePhotosBy(Collection<Photo> photos, double moveBy) {
		for (Photo photo : photos) {
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

	/*
	 * Inserts photos and lays them out in the correct position
	 */
	private void layoutPhotos(Collection<Photo> photosToInsert,
			boolean resetLayout) {

		double rowHeight = 0; // max Y boundary
		double minYBound = Double.MAX_VALUE; // min Y boundary

		Collection<Photo> collagePhotos = getChildrenPhotos();

		/*
		 * Find insert position
		 */
		double insertX = 0;
		double insertY = 0;

		if (!resetLayout) {

			for (Photo photo : collagePhotos) {
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
		}

		/*
		 * Insert Photos
		 */
		for (Photo photo : photosToInsert) {
			// if width exceeded, goto new row at the bottom
			if (insertX + photo.getWidth() > this.getWidth()) {
				insertY += rowHeight;
				insertX = 0;
			}

			if (resetLayout) {
				photo.animateToPosition(insertX, insertY, 1000);
			} else {
				photo.setOffset(insertX, insertY);
				photo.setTransparency(0);
				this.addChild(photo);
				// photo.setTransparency(0);
				addActivity(new Fader(photo, ADD_PHOTO_TIME_MS, 1f));
			}

			insertX += photo.getWidth();

			if (photo.getHeight() > rowHeight) {
				rowHeight = photo.getHeight();
			}
		}

		if (!resetLayout) {
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

				for (Photo photo : collagePhotos) {
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

	@Override
	protected void prepareForDestroy() {
		synchronized (photoQueue) {
			// wake up Insert Photo Thread so it can be destroyed
			photoQueue.notifyAll();
		}
		super.prepareForDestroy();
	}

	public void addAndScrollPhotos(Collection<Photo> photos) {
		layoutPhotos(photos, false);
	}

	public boolean addPhoto(IPhoto p) {
		synchronized (photoQueue) {
			if (photoQueue.size() > 10) {
				return false;
			}

			// create the GPhoto object and wait for the image to load
			Photo photo = new Photo(p);
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

	@Override
	public boolean setWidth(double newWidth) {
		Collection<Photo> collagePhotos = getChildrenPhotos();
		boolean rtnValue = super.setWidth(newWidth);
		layoutPhotos(collagePhotos, true);

		return rtnValue;

	}

	public void removePhoto(Photo photo) {
		this.removeChild(photo);
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

								addAndScrollPhotos((Stack<Photo>) (photoQueue
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
