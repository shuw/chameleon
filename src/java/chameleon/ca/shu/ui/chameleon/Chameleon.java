package ca.shu.ui.chameleon;

import java.awt.event.KeyEvent;

import javax.swing.JMenuBar;

import ca.shu.ui.chameleon.actions.flickr.LoadNetworkAction;
import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs.FlickrDialogException;
import ca.shu.ui.chameleon.objects.PhotoCollage;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.chameleon.world.WorldX;
import ca.shu.ui.lib.AppFrame;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.menus.MenuBuilder;
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
//		PDebug.debugPaintCalls = true;
		PDebug.debugThreads = true;
		UIEnvironment.setDebugEnabled(true);
		new Chameleon();
	}

	public Chameleon() {
		if (myInstance != null) {
			throw new RuntimeException("Only one instance of Chameleon can be running");
		}
		myInstance = this;

		getCanvas().getWorld().getGround().setElasticEnabled(true);
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
	public void initFileMenu(JMenuBar menuBar) {
		MenuBuilder fileMenu = new MenuBuilder("File");
		fileMenu.getJMenu().setMnemonic(KeyEvent.VK_F);

		menuBar.add(fileMenu.getJMenu());

		fileMenu.addAction(new LoadNetworkAction("Open social network", 2, getChameleonHolder()),
				KeyEvent.VK_S);

		fileMenu.addAction(new OpenUserPhotos("Open user photos"), KeyEvent.VK_P);

		fileMenu.addAction(new OpenInterestingPhotos("Open interesting photos"), KeyEvent.VK_P);

	}

	private void openPhotoSource(IStreamingPhotoSource photoSource) {
		PhotoCollage collage = new PhotoCollage(photoSource);

		getWorld().getGround().addChild(collage);
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
