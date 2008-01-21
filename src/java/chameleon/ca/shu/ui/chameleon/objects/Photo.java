package ca.shu.ui.chameleon.objects;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.adapters.flickr.FileDownload;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
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
import ca.shu.ui.lib.world.piccolo.objects.BoundsHandle;
import ca.shu.ui.lib.world.piccolo.objects.Wrapper;
import ca.shu.ui.lib.world.piccolo.primitives.Image;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

import com.aetrion.flickr.photos.Size;

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

	private int currentSize;

	private boolean loadingImage = false;

	private Rectangle2D photoBounds;

	private Image photoImage;

	private final Wrapper photoWrapper;

	private IPhoto proxy;

	private Collection<SearchValuePair> searchableValues;

	public Photo(IPhoto photoData) {
		super(photoData);
		this.proxy = photoData;
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
		builder.addProperty("Title", getModel().getTitle());
		builder.addProperty("Description", getModel().getDescription());
	}

	public boolean acceptTarget(WorldObject target) {
		if (target instanceof WorldLayer) {
			return true;
		} else {
			return false;
		}
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
	public IPhoto getModel() {
		return (IPhoto) super.getModel();
	}

	public IPhoto getProxy() {
		return proxy;
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
			File cachedImage = new File(getImageCacheFolder(proxy.getType()), proxy.getId()
					+ "_Size" + currentSize + ".jpg");

			if (!cachedImage.exists()) {
				/*
				 * Cache the image
				 */
				FileDownload.download(proxy.getImageUrl(currentSize).toString(), cachedImage
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
