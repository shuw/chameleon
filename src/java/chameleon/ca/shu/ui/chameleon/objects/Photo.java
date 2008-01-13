package ca.shu.ui.chameleon.objects;

import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.adapters.flickr.FileDownload;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;

import com.aetrion.flickr.photos.Size;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

/**
 * @author Shu Wu
 */
public class Photo extends ModelObject implements Interactable {

	private static final long serialVersionUID = 1L;

	static final double IMG_BORDER_PX = 50;

	private int currentSize;

	private PNode image;

	private PNode imageHolder;

	private PText loadingText;

	private PhotoInfoFrame photoInfoFrame;

	private IPhoto proxy;

	public Photo(IPhoto photoWr) {
		super(photoWr);
		this.proxy = photoWr;

		imageHolder = new PNode();
		this.addChild(imageHolder);
		setName(getModel().getTitle());
		addInputEventListener(new PhotoEventHandler(this));

		// Sets the default size of photos to the cached photo size
		currentSize = FlickrPhotoSource.DEFAULT_PHOTO_SIZE;

		loadImage();
	}

	@Override
	public IPhoto getModel() {
		return (IPhoto) super.getModel();
	}

	private boolean isPhotoFrameVisible() {
		if (photoInfoFrame != null) {
			return true;
		} else {
			return false;
		}
	}

	private void setPhotoFrameVisible(boolean isVisible) {
		if (isVisible) {
			photoInfoFrame = new PhotoInfoFrame(proxy);

			Point2D framePosition = this.getOffset();

			photoInfoFrame
					.setOffset(framePosition.getX(), framePosition.getY());

			this.addChild(photoInfoFrame);
			photoInfoFrame.animateToPositionScaleRotation(framePosition.getX()
					+ this.getWidth() + 20, framePosition.getY(), 1, 0, 500);
		} else {
			if (photoInfoFrame != null) {
				this.removeChild(photoInfoFrame);
				photoInfoFrame = null;
			}
		}
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

	public IPhoto getProxy() {
		return proxy;
	}

	public boolean isImageLoaded() {
		return (image != null);
	}

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

	private void loadImage() {

		(new Thread() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						loadingText = new PText("Loading Image...");
						loadingText.setOffset(IMG_BORDER_PX, IMG_BORDER_PX);
						loadingText.setFont(Style.FONT_XLARGE);
						loadingText.setPaint(Style.COLOR_BACKGROUND);
						loadingText.setTextPaint(Style.COLOR_FOREGROUND);
						Photo.this.addChild(loadingText);

					}
				});

				PImage imageInner = null;

				/*
				 * Tries to find image in cache first
				 */
				File cachedImage = new File(
						getImageCacheFolder(proxy.getType()), proxy.getId()
								+ "_Size" + currentSize + ".jpg");

				if (!cachedImage.exists()) {
					/*
					 * Cache the image
					 */
					FileDownload.download(proxy.getImageUrl(currentSize)
							.toString(), cachedImage.toString());
				}

				imageInner = new PImage(cachedImage.toString());

				// create a node to hold the image
				image = new PNode();

				imageInner.setOffset(IMG_BORDER_PX, IMG_BORDER_PX);
				image.addChild(imageInner);
				image.setPickable(false);
				image.setChildrenPickable(false);

				image.setBounds(0, 0,
						(float) (imageInner.getWidth() + IMG_BORDER_PX * 2),
						(float) (imageInner.getHeight() + IMG_BORDER_PX * 2));

				// scales the picture

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						loadingText.removeFromParent();
						imageHolder.removeAllChildren();
						imageHolder.addChild(image);

						synchronized (Photo.this) {
							Photo.this.setBounds(Photo.this.globalToLocal(image
									.localToGlobal(image.getBounds())));
							Photo.this.notifyAll();
						}
					}
				});
			}
		}).start();

	}

	class ChangeResolutionAction extends StandardAction {
		private boolean increase;

		public ChangeResolutionAction(String description, boolean increase) {
			super(description);
			this.increase = increase;
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {
			changeResolution(increase);
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
	public String getTypeName() {
		return "Photo";
	}

	@Override
	public void doubleClicked() {
		if (!isPhotoFrameVisible()) {
			setPhotoFrameVisible(true);
		} else {
			setPhotoFrameVisible(false);
		}
	}
}

class PhotoEventHandler extends PBasicInputEventHandler {
	Photo photo;

	public PhotoEventHandler(Photo object) {
		super();
		this.photo = object;
	}

	@Override
	public void mouseEntered(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseEntered(event);
		PBoundsHandle.addBoundsHandlesTo(photo);
	}

	@Override
	public void mouseExited(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseExited(event);

		PBoundsHandle.removeBoundsHandlesFrom(photo);
	}

}
