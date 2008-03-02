package ca.shu.ui.chameleon.objects;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.adapters.IStreamingSourceException;
import ca.shu.ui.chameleon.adapters.SourceEmptyException;
import ca.shu.ui.chameleon.flickr.adapters.FlickrPhoto;
import ca.shu.ui.chameleon.flickr.adapters.FlickrPhotoSource;
import ca.shu.ui.chameleon.util.ChameleonUtil;
import ca.shu.ui.chameleon.util.DistanceListener;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.elastic.ElasticEdge;
import ca.shu.ui.lib.world.elastic.ElasticObject;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

public class SearchTerm extends ElasticObject {

	private FlickrPhotoSource photoSource;
	private Text myText;

	public SearchTerm(String searchTerm, FlickrPhotoSource photoSource) {
		super(searchTerm);
		this.photoSource = photoSource;
		setSelectable(true);

		myText = new Text(searchTerm);
		myText.setScale(5);
		addChild(myText);
		setBounds(parentToLocal(getFullBounds()));

		(new Thread(new Runnable() {
			public void run() {
				loadPhotos();
			}
		}, "Search photo loader")).start();

	}

	static final int MAX_PHOTOS_TO_SHOW_AT_ONCE = 5;
	private LinkedList<Photo> photosAttached = new LinkedList<Photo>();

	class DistanceRemover extends DistanceListener {

		public DistanceRemover(ElasticEdge edge) {
			super(edge);
		}

		@Override
		public void distanceChanged(double distance) {
			if (distance > REMOVE_FROM_HOLDER_DISTANCE) {
				Photo photoToRemove;
				if (getStartNode() instanceof Photo) {
					photoToRemove = ((Photo) getStartNode());
					detachPhoto(photoToRemove);
				}
				if (getEndNode() instanceof Photo) {
					photoToRemove = ((Photo) getEndNode());
					detachPhoto(photoToRemove);
				}

				getEdge().destroy();
				destroy();
			}
		}

	}

	@Override
	public void dragOffset(double dx, double dy) {
		for (Photo photo : photosAttached) {
			photo.dragOffset(dx, dy);
		}
		super.dragOffset(dx, dy);

	}

	protected void detachPhoto(Photo photo) {
		photosAttached.remove(photo);
		photo.showPopupMessage("Detached from " + this.getName());
	}

	private static final double DEFAULT_PHOTO_DISTANCE = 700;
	private static final double REMOVE_FROM_HOLDER_DISTANCE = DEFAULT_PHOTO_DISTANCE * 2;

	private void addPhotoToWorld(Photo photo) {

		Point2D randomOffset = ChameleonUtil.getRandomPointAroundObj(this, DEFAULT_PHOTO_DISTANCE);

		photo.setOffset(localToGlobal(randomOffset));
		photo.setTransparency(0f);
		Fader fader = new Fader(photo, 500, 1f);

		ElasticEdge edge = new ElasticEdge(this, photo, DEFAULT_PHOTO_DISTANCE);

		getWorldLayer().addEdge(edge);
		getWorldLayer().addChild(photo);
		UIEnvironment.getInstance().addActivity(fader);

		photosAttached.add(photo);

		new DistanceRemover(edge);

		if (photosAttached.size() > MAX_PHOTOS_TO_SHOW_AT_ONCE) {
			Point2D center = getBounds().getCenter2D();
			this.localToGlobal(center);

			Photo photoToRemove = photosAttached.removeFirst();
			photoToRemove.animateToPositionScaleRotation(center.getX(), center.getY(),
					photoToRemove.getScale() * 0.3f, photoToRemove.getRotation(), 500);
			ChameleonUtil.FadeAndDestroy(photoToRemove, System.currentTimeMillis(), 500);
		}

	}

	class AddPhotoDelegate implements Runnable {
		private Photo photo;

		public AddPhotoDelegate(Photo photo) {
			super();
			this.photo = photo;
		}

		public void run() {
			addPhotoToWorld(photo);
		}
	}

	private static final int PHOTO_LOAD_DELAY = 3000;

	private void loadPhotos() {

		try {
			while (true) {
				FlickrPhoto photo = photoSource.getPhoto();
				long startTime = System.currentTimeMillis();
				Photo photoObj = new Photo(photo);
				photoObj.waitForPhotoLoad();

				SwingUtilities.invokeLater(new AddPhotoDelegate(photoObj));

				long elaspedTime = System.currentTimeMillis() - startTime;

				if (elaspedTime < PHOTO_LOAD_DELAY) {
					Thread.sleep(PHOTO_LOAD_DELAY - elaspedTime);
				}

			}
		} catch (IStreamingSourceException e) {
			e.printStackTrace();
		} catch (SourceEmptyException e) {
			showPopupMessage("No more photos");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
