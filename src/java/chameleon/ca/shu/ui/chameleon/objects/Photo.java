package ca.shu.ui.chameleon.objects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

import ca.neo.ui.models.tooltips.ITooltipPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.flickr.FlickrAPI;
import ca.shu.ui.chameleon.flickr.adapters.FileDownload;
import ca.shu.ui.chameleon.flickr.adapters.FlickrPhoto;
import ca.shu.ui.chameleon.flickr.adapters.FlickrPhotoSource;
import ca.shu.ui.chameleon.flickr.adapters.FlickrUser;
import ca.shu.ui.chameleon.flickr.adapters.PersonIcon;
import ca.shu.ui.chameleon.util.ChameleonUtil;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Destroyable;
import ca.shu.ui.lib.world.Droppable;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.Searchable;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.WorldObject.Listener;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.objects.BoundsHandle;
import ca.shu.ui.lib.world.piccolo.objects.Wrapper;
import ca.shu.ui.lib.world.piccolo.primitives.Image;
import ca.shu.ui.lib.world.piccolo.primitives.PXEdge;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Size;
import com.aetrion.flickr.photos.comments.Comment;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Shu Wu
 */
public class Photo extends ModelObject implements Interactable, Droppable, Searchable {

	private static final long serialVersionUID = 1L;

	public static final String CACHE_FOLDER_NAME = "ImageCache";

	public static boolean cacheFolderVerified = false;

	private static File getImageCacheFolder(String type) {
		File cacheFolder = new File(CACHE_FOLDER_NAME + "/" + type);

		if (!cacheFolderVerified) {
			cacheFolderVerified = true;
			if (!cacheFolder.exists()) {
				cacheFolder.mkdirs();
			}
		}
		return cacheFolder;
	}

	private CommentLoader commentLoader;

	private int currentSize;

	private boolean loadingImage = false;

	private Rectangle2D photoBounds;

	private Image photoImage;

	private final Wrapper photoWrapper;

	private Collection<SearchValuePair> searchableValues;

	public Photo(FlickrPhoto photoData) {
		super(photoData);
		this.photoWrapper = new PhotoWrapper();
		addChild(photoWrapper);
		photoWrapper.addPropertyChangeListener(Property.BOUNDS_CHANGED, new Listener() {
			public void propertyChanged(Property event) {
				Photo.this.setBounds(photoWrapper.getBounds());
			}
		});
		init(photoData);
	}

	private boolean canChangeResolution(boolean increase) {
		if (increase) {
			if (currentSize < (Size.LARGE)) {
				return true;
			}
		} else {
			if (currentSize > (Size.SMALL)) {
				return true;
			}
		}

		return false;
	}

	private void changeResolution(boolean increase) {
		if (canChangeResolution(increase)) {

			if (increase) {
				currentSize++;
			} else {
				currentSize--;
			}

			loadImage();

		}
	}

	private void init(IPhoto photo) {
		setName(photo.getTitle());

		LinkedList<SearchValuePair> sValues = new LinkedList<SearchValuePair>();
		sValues.add(new SearchValuePair("Title", photo.getTitle()));
		sValues.add(new SearchValuePair("Photo Id", photo.getId()));
		sValues.add(new SearchValuePair("Author Name", photo.getAuthorName()));
		sValues.add(new SearchValuePair("Description", photo.getDescription()));
		this.searchableValues = new ArrayList<SearchValuePair>(sValues);
		// Sets the default size of photos to the cached photo size
		currentSize = FlickrPhotoSource.DEFAULT_PHOTO_SIZE;

		loadImage();
	}

	private void loadImage() {
		if (!loadingImage) {
			loadingImage = true;
			Thread loadImageThread = new Thread(new ImageLoader());
			loadImageThread.start();
		}
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);
		ChameleonMenus.constructMenu(this, getModel(), menu);

		menu.addSection("Photo");

		if (canChangeResolution(true)) {
			menu.addAction(new ChangeResolutionAction("+ Resolution", true));

		}
		if (canChangeResolution(false)) {
			menu.addAction(new ChangeResolutionAction("- Resolution", false));
		}

		if (!isCommentsShown()) {
			menu.addAction(new SetCommentsEnabledAction("Show comments", true));
		} else {
			menu.addAction(new SetCommentsEnabledAction("Hide comments", false));
		}

	}

	class SetCommentsEnabledAction extends StandardAction {
		private boolean enabled;

		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {
			setCommentsVisible(enabled);
		}

		public SetCommentsEnabledAction(String description, boolean enabled) {
			super(description);
			this.enabled = enabled;
		}

	}

	public void setCommentsVisible(boolean visible) {
		if (visible) {
			if (!isCommentsShown()) {
				commentLoader = new CommentLoader(this);
			}
		} else {
			if (isCommentsShown()) {
				commentLoader.destroy();
				commentLoader = null;
			}
		}
	}

	public boolean isCommentsShown() {
		if (commentLoader != null && commentLoader.isAlive()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void constructTooltips(TooltipBuilder builder) {
		super.constructTooltips(builder);

		builder.addPart(new PhotoInfoBar(getModel()));

	}

	public boolean acceptTarget(WorldObject target) {
		if (target instanceof WorldLayer) {
			return true;
		} else if (target instanceof PhotoCollage) {
			return true;
		}
		return false;
	}

	// @Override
	// public void doubleClicked() {
	// if (!isPhotoFrameVisible()) {
	// setPhotoFrameVisible(true);
	// } else {
	// setPhotoFrameVisible(false);
	// }
	// }

	@Override
	public FlickrPhoto getModel() {
		return (FlickrPhoto) super.getModel();
	}

	public Collection<SearchValuePair> getSearchableValues() {
		return searchableValues;
	}

	@Override
	public String getTypeName() {
		return "Photo";
	}

	private boolean isLoaded() {
		return (photoImage != null);
	}

	public void justDropped() {
		// do nothing
	}

	class ChangeResolutionAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		private boolean increase;

		public ChangeResolutionAction(String description, boolean increase) {
			super(description);
			this.increase = increase;
		}

		@Override
		protected void action() throws ActionException {
			changeResolution(increase);
		}

	}

	class ImageLoader implements Runnable {

		public void loadImage() throws InterruptedException, InvocationTargetException {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					Text loadingText = new Text("Loading Image...");
					loadingText.setFont(Style.FONT_XLARGE);
					loadingText.setPaint(Style.COLOR_BACKGROUND);
					loadingText.setTextPaint(Style.COLOR_FOREGROUND);

					if (photoImage != null) {
						photoImage.addChild(loadingText);
					} else {
						photoWrapper.setPackage(loadingText);
					}
				}
			});

			/*
			 * Tries to find image in cache first
			 */
			File cachedImage = new File(getImageCacheFolder(getModel().getType()), getModel()
					.getId()
					+ "_Size" + currentSize + ".jpg");

			if (!cachedImage.exists()) {
				/*
				 * Cache the image
				 */
				FileDownload.download(getModel().getImageUrl(currentSize).toString(), cachedImage
						.toString());
			}

			Rectangle2D oldBounds = null;

			if (photoImage != null) {
				oldBounds = photoImage.getBounds();
				photoImage.destroy();
			}

			photoImage = new Image(cachedImage.toString());

			if (!photoImage.isLoadedSuccessfully()) {
				showPopupMessage("Problem loading this image");
			}

			if (oldBounds != null) {
				photoBounds = photoImage.getBounds();
				photoImage.setBounds(oldBounds);
			}

			// Adds the photo
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					photoWrapper.setPackage(photoImage);

					if (photoBounds != null) {
						photoImage.animateToBounds(photoBounds.getX(), photoBounds.getY(),
								photoBounds.getWidth(), photoBounds.getHeight(), 1000);
					}

					synchronized (Photo.this) {
						Photo.this.notifyAll();
					}
				}
			});
		}

		public void run() {
			try {

				loadImage();
				loadingImage = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void waitForPhotoLoad() {
		while (!isLoaded()) {
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class PhotoResizeHandler extends PBasicInputEventHandler {
		Photo photo;

		public PhotoResizeHandler(Photo object) {
			super();
			this.photo = object;
		}

		@Override
		public void mouseEntered(PInputEvent event) {
			super.mouseEntered(event);
			if (photoImage != null) {
				BoundsHandle.addBoundsHandlesTo(photoImage);
			}
		}

		@Override
		public void mouseExited(PInputEvent event) {
			super.mouseExited(event);
			if (photoImage != null) {
				BoundsHandle.removeBoundsHandlesFrom(photoImage);
			}
		}

	}
}

class CommentLoader implements Destroyable {

	private static final int PERSON_COME_IN_MS = 1000;
	private static final int PERSON_LEAVE_MS = 2000;
	private static final int PERSON_LINGER_MS = 5000;
	private static final int SHOW_COMMENT_DELAY_MS = 4000;

	private List<Comment> comments;
	private Flickr flickrAPI;

	private Photo photoTarget;

	public CommentLoader(Photo photo) {
		this.photoTarget = photo;
		flickrAPI = FlickrAPI.create();
		(new Thread(new Runnable() {
			public void run() {
				loadComments();
				destroy();
			}
		}, "Comment loader")).start();
	}

	private boolean isAlive = true;

	public boolean isAlive() {
		return isAlive;
	}

	private void loadComment(Comment comment) {
		if (photoTarget.isDestroyed()) {
			return;
		}

		long lastCommentShown = System.currentTimeMillis();
		boolean newPerson = false;
		String authorId = comment.getAuthor();
		try {
			Person person = null;
			WorldLayer layer = photoTarget.getWorldLayer();
			if (layer instanceof SocialGround) {
				SocialGround socialGround = (SocialGround) layer;
				person = socialGround.getPerson(authorId);
			}
			if (person == null) {
				/*
				 * Create the person here
				 */
				User user = flickrAPI.getPeopleInterface().getInfo(authorId);
				if (user != null) {
					newPerson = true;
					FlickrUser fUser = new FlickrUser(user);
					person = new Person(fUser);

				}
			} else {
				/*
				 * Person already exists
				 */
			}

			if (person != null) {
				SwingUtilities.invokeAndWait(new ShowCommentRunnable(layer, person, comment,
						newPerson));

			}

		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		long loadTime = System.currentTimeMillis() - lastCommentShown;
		if (loadTime < SHOW_COMMENT_DELAY_MS) {
			try {
				Thread.sleep(SHOW_COMMENT_DELAY_MS - loadTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadComments() {

		if (comments == null) {
			comments = new ArrayList<Comment>(0);
			try {
				comments = photoTarget.getModel().getComments();
			} catch (FlickrException e) {
				e.printStackTrace();
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (comments.size() > 0) {
					photoTarget.showPopupMessage(comments.size() + " comments found here");
				} else {
					photoTarget.showPopupMessage("No comments here");
				}
			}
		});

		for (Comment comment : comments) {
			if (!photoTarget.isDestroyed()) {
				loadComment(comment);
			} else {
				return;
			}
		}
	}

	private static final double NEW_PERSON_DROP_DISTANCE = 800;

	private void showComment(WorldLayer layer, Person person, Comment comment, boolean newPerson)
			throws InterruptedException, InvocationTargetException {
		if (person.isDestroyed() || !isAlive) {
			return;
		}

		if (newPerson) {
			person.setTransparency(0f);

			layer.addChild(person);

			Point2D offset = photoTarget.localToGlobal(new Point2D.Double(photoTarget.getBounds()
					.getCenterX(), photoTarget.getBounds().getMinY() - NEW_PERSON_DROP_DISTANCE));
			person.setOffset(offset);

			UIEnvironment.getInstance().addActivity(new Fader(person, PERSON_COME_IN_MS, 1f));
		}

		/*
		 * Find position for the person to appear in
		 */

		double randomRotation = ((new Random()).nextDouble() - 0.5d) * 0.4d;

		Point2D newOffset = photoTarget.localToGlobal(ChameleonUtil
				.getRandomPointAroundObj(photoTarget));

		Point2D originalOffset = person.getOffset();
		double originalScale = person.getScale();
		double originalRotation = person.getRotation();
		person.animateToPositionScaleRotation(newOffset.getX(), newOffset.getY(),
				originalScale * 1.5d, randomRotation, PERSON_COME_IN_MS);
		person.animateToPositionScaleRotation(newOffset.getX(), newOffset.getY() + 20,
				originalScale, originalRotation, PERSON_LINGER_MS);
		person.animateToPosition(originalOffset.getX(), originalOffset.getY(), PERSON_LEAVE_MS);

		CommentText commentObj = new CommentText(photoTarget, person, comment.getText());
		long startFadingTime = System.currentTimeMillis() + PERSON_COME_IN_MS + PERSON_LINGER_MS;

		ChameleonUtil.FadeAndDestroy(commentObj, startFadingTime, PERSON_LEAVE_MS);

		if (newPerson) {
			/*
			 * If we created the person, then remove them again
			 */
			ChameleonUtil.FadeAndDestroy(person, startFadingTime, PERSON_LEAVE_MS);

		}

	}

	class ShowCommentRunnable implements Runnable {
		private Comment comment;
		private WorldLayer layer;
		private boolean newPerson;
		private Person person;

		public ShowCommentRunnable(WorldLayer layer, Person person, Comment comment,
				boolean newPerson) {
			super();
			this.layer = layer;
			this.person = person;
			this.comment = comment;
			this.newPerson = newPerson;
		}

		public void run() {
			try {
				showComment(layer, person, comment, newPerson);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.getTargetException().printStackTrace();
			}
		}

	}

	public void destroy() {
		isAlive = false;

	}
}

class CommentText extends Text implements Listener {
	private static final Color[] COMMENT_COLORS = { Style.COLOR_LIGHT_BLUE,
			Style.COLOR_LIGHT_GREEN, Style.COLOR_LIGHT_PURPLE };
	private static final double COMMENT_WIDTH = 200;

	private Person author;
	private Photo target;
	private PXEdge edgeToPhoto;

	public CommentText(Photo target, Person author, String text) {
		super(Util.truncateString(text, 50));
		this.author = author;
		this.target = target;
		init();
	}

	private void init() {
		setFont(Style.FONT_LARGE);
		author.getWorldLayer().addChild(this);

		/*
		 * Create edge holder to which holds the edge at the top left corner of
		 * the comment
		 */
		WorldObjectImpl edgeHolder = new WorldObjectImpl();
		edgeToPhoto = new PXEdge(target, edgeHolder);
		edgeToPhoto.setStrokePaint(Style.COLOR_BACKGROUND2);

		setConstrainWidthToTextWidth(false);
		setWidth(COMMENT_WIDTH);
		recomputeLayout();

		double random = (new Random()).nextDouble() * COMMENT_COLORS.length;

		for (int count = 0; count < COMMENT_COLORS.length; count++) {
			if (random < ((double) count + 1d)) {
				setTextPaint(COMMENT_COLORS[count]);
				break;
			}

		}
		author.addPropertyChangeListener(Property.GLOBAL_BOUNDS, this);
		author.addPropertyChangeListener(Property.REMOVED_FROM_WORLD, this);

		addChild(edgeHolder);
		target.getPiccolo().addChild(0, edgeToPhoto);
		updatePosition();
	}

	private void updatePosition() {
		Point2D position = new Point2D.Double(author.getWidth() * (0.3f), author.getHeight() + 5);
		position = author.localToGlobal(position);
		setOffset(position);

	}

	@Override
	protected void prepareForDestroy() {
		edgeToPhoto.destroy();
		author.removePropertyChangeListener(Property.REMOVED_FROM_WORLD, this);
		author.removePropertyChangeListener(Property.GLOBAL_BOUNDS, this);
		super.prepareForDestroy();
	}

	public void propertyChanged(Property event) {
		if (event == Property.REMOVED_FROM_WORLD) {
			destroy();
		} else if (event == Property.GLOBAL_BOUNDS) {
			updatePosition();
		}
	}
}

/**
 * Infor bar which shows the author, name , date url
 * 
 * @author Shu Wu
 */
class PhotoInfoBar implements ITooltipPart {

	private double offsetY;

	private IPhoto photo;

	private WorldObject tooltipObj;

	private double tooltipWidth;

	public PhotoInfoBar(IPhoto photo) {
		super();
		this.photo = photo;
	}

	private void addText(double offsetX, Text text) {
		if (tooltipWidth - offsetX > 20) {
			text.setConstrainWidthToTextWidth(false);
			text.setWidth(tooltipWidth - offsetX);
			text.recomputeLayout();
		}

		text.setOffset(offsetX, offsetY);
		tooltipObj.addChild(text);
		offsetY += text.getHeight();

	}

	public WorldObject toWorldObject(double width) {
		this.tooltipObj = new WorldObjectImpl();
		this.tooltipWidth = width;

		PersonIcon personIcon = new PersonIcon(photo.getOwnerProfilePicUrl());
		tooltipObj.addChild(personIcon);

		double offsetX = 55;
		offsetY = 0;
		Text name = new Text("By " + photo.getOwnerName());
		Text location = new Text("From " + photo.getOwnerLocation());
		Text Date = new Text("Taken on " + photo.getDateTaken());
		// Text Url = new Text("Url: " + photo.getUrl().toString());

		name.setFont(Style.FONT_BOLD);

		addText(offsetX, name);
		addText(offsetX, location);
		addText(offsetX, Date);
		// addText(offsetX, Url);

		if (photo.getDescription() != null) {
			String noHTMLString = Util.truncateString(photo.getDescription(), 200);
			Text description = new Text(noHTMLString);

			addText(0, description);
			description.translate(0, 5);
		}

		tooltipObj.setBounds(tooltipObj.parentToLocal(tooltipObj.getFullBounds()));
		return tooltipObj;
	}

}

class PhotoWrapper extends Wrapper implements Listener {
	static final double IMG_BORDER_PX = 50;

	public PhotoWrapper() {
		super(null);
	}

	private void resize() {
		WorldObject imageInner = getPackage();

		setBounds(0, 0, (float) (imageInner.getWidth() + IMG_BORDER_PX * 2), (float) (imageInner
				.getHeight() + IMG_BORDER_PX * 2));

	}

	@Override
	protected void packageChanged(WorldObject oldPackage) {
		if (oldPackage != null) {
			oldPackage.removePropertyChangeListener(Property.BOUNDS_CHANGED, this);
		}

		getPackage().setOffset(IMG_BORDER_PX, IMG_BORDER_PX);
		getPackage().addPropertyChangeListener(Property.BOUNDS_CHANGED, this);
		resize();
	}

	public void propertyChanged(Property event) {
		resize();
	}

}