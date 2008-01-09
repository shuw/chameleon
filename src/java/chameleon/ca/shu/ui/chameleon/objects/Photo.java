package ca.shu.ui.chameleon.objects;

import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.chameleon.adapters.flickr.FileDownload;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
import ca.shu.ui.lib.Style.Style;
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

public class Photo extends ModelObject implements Interactable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final double IMG_BORDER_PX = 50;

	private int currentSize = 0;

	private PNode image;

	private PNode imageHolder;

	private PText loadingText;

	private GPhotoInfoFrame photoInfoFrame;

	private IPhoto proxy;

	public Photo(IPhoto photoWr) {
		super(photoWr);
		this.proxy = photoWr;

		// Sets the default size of photos to the cached photo size
		currentSize = FlickrPhotoSource.DEFAULT_PHOTO_SIZE;

		imageHolder = new PNode();
		this.addChild(imageHolder);
		setName(getModel().getTitle());

		(new Thread() {
			@Override
			public void run() {
				loadImage(currentSize);
			}
		}).start();
	}

	@Override
	public IPhoto getModel() {
		return (IPhoto) super.getModel();
	}

	public void createInfoFrame() {

		photoInfoFrame = new GPhotoInfoFrame(proxy);

		Point2D framePosition = this.getOffset();

		photoInfoFrame.setOffset(framePosition.getX(), framePosition.getY());

		this.addChild(photoInfoFrame);
		photoInfoFrame.animateToPositionScaleRotation(framePosition.getX()
				+ this.getWidth() + 20, framePosition.getY(), 1, 0, 500);
	}

	public void getHigherResolution() {

		if (currentSize > (Size.LARGE)) {
			return;
		} else {
			currentSize++;
		}

		(new Thread() {
			@Override
			public void run() {
				loadImage(currentSize);
			}
		}).start();

	}

	public IPhoto getProxy() {
		return proxy;
	}

	public void hideInfoFrame() {
		if (photoInfoFrame != null) {
			this.removeChild(photoInfoFrame);
			photoInfoFrame = null;
		}
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

	public void loadImage(int size) {
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
		File cachedImage = new File(getImageCacheFolder(proxy.getType()), proxy
				.getId()
				+ "_Size" + size + ".jpg");

		if (!cachedImage.exists()) {
			/*
			 * Cache the image
			 */
			FileDownload.download(proxy.getImageUrl(size).toString(),
					cachedImage.toString());
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

	public void toggleInfoFrame() {
		if (photoInfoFrame == null) {
			createInfoFrame();
		} else {
			hideInfoFrame();
		}
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {

		// AbstractButton magnifyBtn = new TextButton("+", new Runnable() {
		// public void run() {
		// photo.animateToScale(photo.getScale() * SCALE_FACTOR,
		// ANIMATE_TIME_MS);
		//
		// }
		// });
		// AbstractButton shrinkBtn = new TextButton("-", new Runnable() {
		// public void run() {
		// photo.animateToScale(photo.getScale() / SCALE_FACTOR,
		// ANIMATE_TIME_MS);
		// }
		// });
		//
		// AbstractButton getHigherResolution = new TextButton("+Resolution",
		// new Runnable() {
		// public void run() {
		// photo.getHigherResolution();
		// }
		// });
		//
		// addButton(new TextButton("Open In Browser", new Runnable() {
		// public void run() {
		// FlickrPhoto flickrPhoto = (FlickrPhoto) photo.getProxy();
		//
		// Util.openURL(flickrPhoto.photo.getUrl());
		// // try {
		// // showURL(new URL(flickrPhoto.photo.getUrl()));
		// // } catch (MalformedURLException e) {
		// // // TODO Auto-generated catch block
		// // e.printStackTrace();
		// // }
		// }
		// }));
		//
		// // Button getHigherResolution = new GButton("+", new Runnable() {
		// // public void run() {
		// // javax.
		// // jnlp.BasicService.showDocument
		// // }
		// // });
		//
		// addButton(magnifyBtn);
		// addButton(shrinkBtn);
		// addButton(getHigherResolution);
		//
		// addButton(new GButton("close", new Runnable() {
		// public void run() {
		// photo.removeFromParent();
		// }
		// }));

		// TODO Auto-generated method stub
		super.constructMenu(menu);
	}

	@Override
	public String getTypeName() {
		return "Photo";
	}
}

class PhotoEventHandler extends PBasicInputEventHandler {
	Photo photo;

	public PhotoEventHandler(Photo object) {
		super();
		this.photo = object;
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseClicked(event);

		if (event.getClickCount() == 2) {
			// System.out.println("double click");

			photo.toggleInfoFrame();

		}
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
