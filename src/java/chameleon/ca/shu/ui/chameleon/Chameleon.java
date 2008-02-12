package ca.shu.ui.chameleon;

import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import ca.shu.ui.chameleon.actions.flickr.LoadNetworkAction;
import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs.FlickrDialogException;
import ca.shu.ui.chameleon.objects.PhotoCollage;
import ca.shu.ui.chameleon.objects.SearchTerm;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.chameleon.world.WorldX;
import ca.shu.ui.lib.AppFrame;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.elastic.ElasticWorld;
import edu.umd.cs.piccolo.util.PDebug;

public class Chameleon extends AppFrame {

	private static Chameleon myInstance;

	private static final long serialVersionUID = 1L;

	public static final String CONFIG_FILE = "settings.conf";

	static final int TEST_SIZE = 10;

	public static Chameleon getInstance() {
		return myInstance;
	}

	public static void main(String[] args) throws Exception {
		// PDebug.debugPaintCalls = true;

		PDebug.debugThreads = true;
		UIEnvironment.setDebugEnabled(true);
		new Chameleon();
	}

	public Chameleon() {
		if (myInstance != null) {
			throw new RuntimeException("Only one instance of Chameleon can be running");
		}
		myInstance = this;
		(new HighQualityAction()).doAction();
		getUniverse().getWorld().getGround().setElasticEnabled(true);
	}

	@Override
	public String getAboutString() {
		return getAppName() + "<BR>Copyright 2007 by Shu Wu, shuwu83@gmail.com";
	}

	@Override
	public String getAppName() {
		return "Chameleon V0.1";
	}

	@Override
	public String getAppWindowTitle() {
		return getAppName();
	}

	@Override
	public void initFileMenu(MenuBuilder fileMenu) {

		fileMenu.addAction(new SearchPhotos("Search photos"), KeyEvent.VK_S);

		fileMenu.addAction(new LoadNetworkAction("Open person", 2, getChameleonHolder()),
				KeyEvent.VK_P);

		fileMenu.addAction(new OpenUserPhotos("Open person's photos"), KeyEvent.VK_H);

		fileMenu.addAction(new OpenInterestingPhotos("Open interesting photos"), KeyEvent.VK_I);

	}

	private void openPhotoSource(IStreamingPhotoSource photoSource) {
		PhotoCollage collage = new PhotoCollage(photoSource);
		collage.setScale(0.7f);
		addChildChameleon(collage);

	}

	static double childLayoutX = 0;
	static double childLayoutY = 0;

	private void addChildChameleon(WorldObject obj) {
		double horizontalDropDistance = 600;
		obj.setTransparency(0f);
		obj.setOffset(childLayoutX, childLayoutY - horizontalDropDistance);

		getWorld().getGround().addChild(obj);

		Fader fader = new Fader(obj, 1000, 1f);
		obj.animateToPositionScaleRotation(childLayoutX, childLayoutY, obj.getScale(), 0, 1000);
		getWorld().getGround().addActivity(fader);

		Rectangle2D bounds = obj.getParent().localToGlobal(obj.getFullBounds());
		Rectangle2D finalBounds = new Rectangle2D.Double(bounds.getX(), bounds.getY()
				+ horizontalDropDistance, bounds.getWidth(), bounds.getHeight());

		getWorld().zoomToBounds(finalBounds);

		childLayoutX += 500;
		if (childLayoutX > 1500) {
			childLayoutY += 500;
			childLayoutX = 0;
		}
	}

	class OpenInterestingPhotos extends StandardAction {

		public OpenInterestingPhotos(String description) {
			super(description);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {
			openPhotoSource(FlickrPhotoSource.createInterestingSource());
		}

	}

	class SearchPhotos extends StandardAction {

		public SearchPhotos(String description) {
			super(description);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {
			String searchTerm;
			try {
				searchTerm = FlickrDialogs.askSearchTerm();
				FlickrPhotoSource photoSource = FlickrPhotoSource.createSearchSource(searchTerm);

				SearchTerm searchFrame = new SearchTerm(searchTerm, photoSource);

				addChildChameleon(searchFrame);
			} catch (FlickrDialogException e) {
				throw new UserCancelledException();
			}

		}

	}

	class OpenUserPhotos extends StandardAction {

		private static final long serialVersionUID = 1L;

		public OpenUserPhotos(String description) {
			super(description);
		}

		@Override
		protected void action() throws ActionException {
			try {
				String userName = FlickrDialogs.askUserName();

				FlickrPhotoSource source = FlickrPhotoSource.createUserSource(userName);
				openPhotoSource(source);

			} catch (FlickrDialogException e) {
				throw new UserCancelledException();
			}
		}

	}

	@Override
	protected ElasticWorld createWorld() {
		return new WorldX();
	}

	private SocialGround getChameleonHolder() {
		return (SocialGround) getWorld().getGround();
	}

}
