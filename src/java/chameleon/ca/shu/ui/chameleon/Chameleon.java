package ca.shu.ui.chameleon;

import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.flickr.FlickrDialogs;
import ca.shu.ui.chameleon.flickr.actions.LoadNetworkAction;
import ca.shu.ui.chameleon.flickr.adapters.FlickrPhotoSource;
import ca.shu.ui.chameleon.objects.PhotoCollage;
import ca.shu.ui.chameleon.objects.SearchTerm;
import ca.shu.ui.chameleon.spaceWalk.actions.StartSpaceWalk;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.chameleon.world.WorldX;
import ca.shu.ui.lib.AppFrame;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages.DialogException;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.elastic.ElasticWorld;
import edu.umd.cs.piccolo.util.PDebug;

public class Chameleon extends AppFrame {

	class OpenInterestingPhotos extends StandardAction {

		private static final long serialVersionUID = 1L;

		public OpenInterestingPhotos(String description) {
			super(description);
		}

		@Override
		protected void action() throws ActionException {
			openPhotoSource(FlickrPhotoSource.createInterestingSource());
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

				FlickrPhotoSource source = FlickrPhotoSource
						.createUserSource(userName);
				openPhotoSource(source);

			} catch (DialogException e) {
				throw new UserCancelledException();
			}
		}

	}

	class SearchPhotos extends StandardAction {

		private static final long serialVersionUID = 1L;

		public SearchPhotos(String description) {
			super(description);
		}

		@Override
		protected void action() throws ActionException {
			String searchTerm;
			try {
				searchTerm = FlickrDialogs.askSearchTerm();
				FlickrPhotoSource photoSource = FlickrPhotoSource
						.createSearchSource(searchTerm);

				SearchTerm searchFrame = new SearchTerm(searchTerm, photoSource);

				addChildChameleon(searchFrame);
			} catch (DialogException e) {
				throw new UserCancelledException();
			}

		}

	}

	private static double childLayoutX = 0;

	private static double childLayoutY = 0;

	public static final String CONFIG_FILE = "settings.conf";

	private static Chameleon myInstance;

	private static final long serialVersionUID = 1L;

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
			throw new RuntimeException(
					"Only one instance of Chameleon can be running");
		}
		myInstance = this;
		(new HighQualityAction()).doAction();
		getUniverse().getWorld().getGround().setElasticEnabled(true);
	}

	private void addChildChameleon(WorldObject obj) {
		double horizontalDropDistance = 600;
		obj.setTransparency(0f);
		obj.setOffset(childLayoutX, childLayoutY - horizontalDropDistance);

		getWorld().getGround().addChild(obj);

		Fader fader = new Fader(obj, 1000, 1f);
		obj.animateToPositionScaleRotation(childLayoutX, childLayoutY, obj
				.getScale(), 0, 1000);
		UIEnvironment.getInstance().addActivity(fader);

		Rectangle2D bounds = obj.getParent().localToGlobal(obj.getFullBounds());
		Rectangle2D finalBounds = new Rectangle2D.Double(bounds.getX(), bounds
				.getY()
				+ horizontalDropDistance, bounds.getWidth(), bounds.getHeight());

		getWorld().zoomToBounds(finalBounds);

		childLayoutX += 500;
		if (childLayoutX > 1500) {
			childLayoutY += 500;
			childLayoutX = 0;
		}
	}

	@Override
	protected ElasticWorld createWorld() {
		return new WorldX();
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

	private SocialGround getChameleonHolder() {
		return (SocialGround) getWorld().getGround();
	}

	@Override
	public void initFileMenu(MenuBuilder fileMenu) {
		MenuBuilder flickrMenu = fileMenu.addSubMenu("Start Flickr");
		fileMenu.addAction(new StartSpaceWalk("Start SpaceWalk",
				getChameleonHolder()));

		flickrMenu.addAction(new SearchPhotos("Search photos"), KeyEvent.VK_S);

		flickrMenu.addAction(new LoadNetworkAction("Open person", 2,
				getChameleonHolder()), KeyEvent.VK_P);

		flickrMenu.addAction(new OpenUserPhotos("Open person's photos"),
				KeyEvent.VK_H);

		flickrMenu.addAction(new OpenInterestingPhotos(
				"Open interesting photos"), KeyEvent.VK_I);

	}

	private void openPhotoSource(IStreamingPhotoSource photoSource) {
		PhotoCollage collage = new PhotoCollage(photoSource);
		collage.setScale(0.7f);
		addChildChameleon(collage);

	}

}
