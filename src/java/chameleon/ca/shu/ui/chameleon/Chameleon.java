package ca.shu.ui.chameleon;

import java.awt.event.KeyEvent;
import java.security.InvalidParameterException;
import java.util.Hashtable;

import javax.swing.JMenuBar;

import ca.shu.ui.chameleon.actions.flickr.NetworkBuilder;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
import ca.shu.ui.chameleon.adapters.flickr.FlickrDialogs.FlickrDialogException;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.objects.PhotoCollage;
import ca.shu.ui.lib.AppFrame;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.PEdge;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.menus.MenuBuilder;

public class Chameleon extends AppFrame {

	private static Chameleon myInstance;

	private static final long serialVersionUID = 1L;

	public static final String CONFIG_FILE = "settings.conf";

	static final int TEST_SIZE = 10;

	public static Chameleon getInstance() {
		return myInstance;
	}

	public static void main(String[] args) throws Exception {
		UIEnvironment.setDebugEnabled(true);
		new Chameleon();
	}

	Hashtable<String, Person> personTable = new Hashtable<String, Person>();

	public Chameleon() {
		if (myInstance != null) {
			throw new RuntimeException(
					"Only one instance of Chameleon can be running");
		}
		myInstance = this;

		getCanvas().getWorld().getGround().setElasticEnabled(true);
	}

	public void addPerson(Person person) {
		if (personTable.get(person.getId()) != null) {
			throw new InvalidParameterException();
		}
		personTable.put(person.getId(), person);
		getCanvas().getWorld().getGround().addChild(person);
	}

	public void addRelationship(String id_personA, String id_personB) {
		Person personA = getPerson(id_personA);
		Person personB = getPerson(id_personB);

		PEdge edge = new PEdge(personA, personB, false);

		getWorld().getGround().addEdge(edge);

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

	public Person getPerson(String id) {
		return personTable.get(id);
	}

	@Override
	public void initFileMenu(JMenuBar menuBar) {
		MenuBuilder fileMenu = new MenuBuilder("File");
		fileMenu.getJMenu().setMnemonic(KeyEvent.VK_F);

		menuBar.add(fileMenu.getJMenu());

		fileMenu.addAction(new NetworkBuilder("Open social network"),
				KeyEvent.VK_S);

		fileMenu.addAction(new OpenUserPhotos("Open user photos"),
				KeyEvent.VK_P);

	}

	class OpenUserPhotos extends StandardAction {

		public OpenUserPhotos(String description) {
			super(description);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {
			try {
				String userName = FlickrDialogs.askUserName();

				FlickrPhotoSource source = FlickrPhotoSource
						.createUserSource(userName);
				PhotoCollage collage = new PhotoCollage(source);

				getWorld().getGround().addChild(collage);

			} catch (FlickrDialogException e) {
				throw new UserCancelledException();
			}
		}

	}

}
