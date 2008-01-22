package ca.shu.ui.chameleon.objects;

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

import util.ChameleonUtil;
import ca.neo.ui.models.tooltips.ITooltipPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.adapters.flickr.FileDownload;
import ca.shu.ui.chameleon.adapters.flickr.FlickrAPI;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhoto;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
import ca.shu.ui.chameleon.adapters.flickr.FlickrUser;
import ca.shu.ui.chameleon.adapters.flickr.PersonIcon;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Droppable;
import ca.shu.ui.lib.world.EventListener;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.Searchable;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.objects.BoundsHandle;
import ca.shu.ui.lib.world.piccolo.objects.Wrapper;
import ca.shu.ui.lib.world.piccolo.primitives.Image;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Size;
import com.aetrion.flickr.photos.comments.Comment;

import edu.umd.cs.piccolo.activities.PActivity;
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
		photoWrapper.addPropertyChangeListener(EventType.BOUNDS_CHANGED, new EventListener() {
			public void propertyChanged(EventType event) {
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

		if (canChangeResolution(true)) {
			menu.addAction(new ChangeResolutionAction("+ Resolution", true));

		}
		if (canChangeResolution(false)) {
			menu.addAction(new ChangeResolutionAction("- Resolution", false));
		}
	}

	@Override
	protected void constructTooltips(TooltipBuilder builder) {
		super.constructTooltips(builder);

		builder.addPart(new PhotoInfoBar(getModel()));

		if (commentLoader == null && getParent() instanceof SocialGround) {
			commentLoader = new CommentLoader(this);
		}
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

	public boolean isLoaded() {
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

class CommentLoader {
	private static final double COMMENT_WIDTH = 200;
	private static final int PERSON_COME_IN_MS = 1000;
	private static final int PERSON_LEAVE_MS = 2000;
	private static final int PERSON_LINGER_MS = 5000;
	private static final int SHOW_COMMENT_DELAY_MS = 4000;

	private List<Comment> comments;
	private Flickr flickrAPI;

	private Photo photoParent;

	public CommentLoader(Photo photo) {
		this.photoParent = photo;
		flickrAPI = FlickrAPI.create();
		(new Thread(new Runnable() {
			public void run() {
				loadComments();
			}
		})).start();
	}

	private void loadComment(Comment comment) {
		if (photoParent.isDestroyed()) {
			return;
		}

		long lastCommentShown = System.currentTimeMillis();
		boolean newPerson = false;
		String authorId = comment.getAuthor();
		try {
			Person person = null;
			WorldLayer layer = photoParent.getWorldLayer();
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
				comments = photoParent.getModel().getComments();
			} catch (FlickrException e) {
				e.printStackTrace();
			}
		}

		for (Comment comment : comments) {
			if (!photoParent.isDestroyed()) {
				loadComment(comment);
			} else {
				return;
			}
		}
	}

	private void showComment(WorldLayer layer, Person person, Comment comment, boolean newPerson)
			throws InterruptedException, InvocationTargetException {
		if (person.isDestroyed()) {
			return;
		}

		if (newPerson) {
			person.setTransparency(0f);

			layer.addChild(person);

			Point2D offset = photoParent.localToGlobal(new Point2D.Double(photoParent.getBounds()
					.getCenterX(), photoParent.getBounds().getMinY() - 300));
			person.setOffset(offset);

			person.addActivity(new Fader(person, PERSON_COME_IN_MS, 1f));
		}

		/*
		 * Find position for the person to appear in
		 */
		double radius = photoParent.getWidth();
		if (radius < photoParent.getHeight()) {
			radius = photoParent.getHeight();
		}
		Random random = new Random();
		double randomAngle = random.nextDouble() * 2d * Math.PI;
		double randomOffset = ((random.nextDouble() - 0.5d) * 0.2d) + 1d;
		double randomRotation = (random.nextDouble() - 0.5d) * 0.4d;

		double offsetY = (Math.sin(randomAngle) * radius) * randomOffset;
		double offsetX = (Math.cos(randomAngle) * radius) * randomOffset;
		offsetX += photoParent.getBounds().getCenterX();
		offsetY += photoParent.getBounds().getCenterY();

		Point2D newOffset = photoParent.localToGlobal(new Point2D.Double(offsetX, offsetY));

		Point2D originalOffset = person.getOffset();
		double originalScale = person.getScale();
		double originalRotation = person.getRotation();
		person.animateToPositionScaleRotation(newOffset.getX(), newOffset.getY(),
				originalScale * 1.5d, randomRotation, PERSON_COME_IN_MS);
		person.animateToPositionScaleRotation(newOffset.getX(), newOffset.getY() + 20,
				originalScale, originalRotation, PERSON_LINGER_MS);
		person.animateToPosition(originalOffset.getX(), originalOffset.getY(), PERSON_LEAVE_MS);

		CommentText commentObj = new CommentText(person, comment.getText());
		long endCommentTime = System.currentTimeMillis() + PERSON_COME_IN_MS + PERSON_LINGER_MS
				+ PERSON_LEAVE_MS;

		PActivity destroyComment = new DestroyActivity(commentObj);
		destroyComment.setStartTime(endCommentTime);

		Fader fadeComment = new Fader(commentObj, PERSON_LEAVE_MS, 0f);
		fadeComment.setStartTime(endCommentTime - PERSON_LEAVE_MS);

		layer.addActivity(destroyComment);
		layer.addActivity(fadeComment);

		if (newPerson) {
			/*
			 * If we created the person, then remove them again
			 */

			PActivity destroyPerson = new DestroyActivity(person);
			Fader fadePerson = new Fader(person, PERSON_LEAVE_MS, 0f);
			fadePerson.setStartTime(endCommentTime - PERSON_LEAVE_MS);

			destroyPerson.setStartTime(endCommentTime);

			layer.addActivity(destroyPerson);
			layer.addActivity(fadePerson);
		}

	}

	class CommentText extends Text implements EventListener {
		private Person author;

		public CommentText(Person author, String text) {
			super(ChameleonUtil.processString(text, 50));
			this.author = author;
			init();
		}

		private void init() {
			setFont(Style.FONT_LARGE);

			author.getWorldLayer().addChild(this);

			setConstrainWidthToTextWidth(false);
			setWidth(COMMENT_WIDTH);
			recomputeLayout();

			double random = (new Random()).nextDouble() * 300;
			if (random > 200) {
				setTextPaint(Style.COLOR_LIGHT_BLUE);
			} else if (random > 100) {
				setTextPaint(Style.COLOR_LIGHT_GREEN);
			} else {
				setTextPaint(Style.COLOR_LIGHT_PURPLE);
			}
			author.addPropertyChangeListener(EventType.GLOBAL_BOUNDS, this);
			author.addPropertyChangeListener(EventType.REMOVED_FROM_WORLD, this);

			updatePosition();
		}

		private void updatePosition() {
			Point2D position = new Point2D.Double(author.getWidth() * (0.3f),
					author.getHeight() + 5);
			position = author.localToGlobal(position);
			setOffset(position);

		}

		@Override
		protected void prepareForDestroy() {
			author.removePropertyChangeListener(EventType.REMOVED_FROM_WORLD, this);
			author.removePropertyChangeListener(EventType.GLOBAL_BOUNDS, this);
			super.prepareForDestroy();
		}

		public void propertyChanged(EventType event) {
			if (event == EventType.REMOVED_FROM_WORLD) {
				destroy();
			} else if (event == EventType.GLOBAL_BOUNDS) {
				updatePosition();
			}
		}
	}

	class DestroyActivity extends PActivity {
		private WorldObject obj;

		public DestroyActivity(WorldObject obj) {
			super(0);
			this.obj = obj;
		}

		@Override
		protected void activityStarted() {
			obj.destroy();
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
			String noHTMLString = ChameleonUtil.processString(photo.getDescription(), 200);
			Text description = new Text(noHTMLString);

			addText(0, description);
			description.translate(0, 5);
		}

		tooltipObj.setBounds(tooltipObj.parentToLocal(tooltipObj.getFullBounds()));
		return tooltipObj;
	}

}

class PhotoWrapper extends Wrapper implements EventListener {
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
			oldPackage.removePropertyChangeListener(EventType.BOUNDS_CHANGED, this);
		}

		getPackage().setOffset(IMG_BORDER_PX, IMG_BORDER_PX);
		getPackage().addPropertyChangeListener(EventType.BOUNDS_CHANGED, this);
		resize();
	}

	public void propertyChanged(EventType event) {
		resize();
	}

}