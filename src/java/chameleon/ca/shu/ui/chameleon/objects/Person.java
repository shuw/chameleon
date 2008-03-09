package ca.shu.ui.chameleon.objects;

import java.awt.geom.Point2D;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.chameleon.adapters.IPersonItemInfo;
import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.chameleon.flickr.actions.ExpandNetworkAction;
import ca.shu.ui.chameleon.flickr.adapters.FlickrPhotoSource;
import ca.shu.ui.chameleon.flickr.adapters.PersonIcon;
import ca.shu.ui.chameleon.world.ChameleonStyle;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.Searchable;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.elastic.ElasticWorld;
import ca.shu.ui.lib.world.piccolo.objects.RectangularEdge;
import ca.shu.ui.lib.world.piccolo.objects.Window;
import ca.shu.ui.lib.world.piccolo.objects.Window.WindowState;

public class Person extends ModelObject implements Interactable, Searchable {

	class NavigateToFriendAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		private Person friend;

		public NavigateToFriendAction(String description, Person friend) {
			super(description);
			this.friend = friend;
		}

		@Override
		protected void action() throws ActionException {
			friend.getWorld().getSky().animateViewToCenterBounds(
					friend.localToGlobal(friend.getBounds()), false, 1000);
		}
	}

	class SetPhotosEnabledAction extends StandardAction {

		private static final long serialVersionUID = 1L;
		private boolean enabled;

		public SetPhotosEnabledAction(String description, boolean enabled) {
			super(description);
			this.enabled = enabled;
		}

		@Override
		protected void action() throws ActionException {
			setPhotosEnabled(enabled);
		}

	}

	class SetWindowEnabled extends StandardAction {
		private static final long serialVersionUID = 1L;

		private boolean enabled;

		public SetWindowEnabled(String description, boolean enabled) {
			super(description);
			this.enabled = enabled;
		}

		@Override
		protected void action() throws ActionException {
			setWindowEnabled(enabled);
		}

	}

	private static final long serialVersionUID = 1L;

	private RectangularEdge collageShadow = null;

	private HashSet<Person> friends;

	private LinkedList<PersonItem> items;
	private Hashtable<String, PersonItem> itemsTable;

	public final int MAX_FRIENDS_TO_SHOW_IN_MENU = 20;

	private PersonIcon myIcon;

	private PhotoCollage myPhotoCollage = null;

	private WeakReference<Window> windowRef = new WeakReference<Window>(null);

	public Person(IUser user) {
		super(user);
		friends = new HashSet<Person>();
		init(user);
	}

	public void addFriend(Person person) {
		if (!friends.contains(person)) {
			friends.add(person);
		}
	}

	public PersonItem addItem(IPersonItemInfo info) {
		PersonItem item = itemsTable.get(info.getId());
		if (item == null) {
			item = new PersonItem(info);

			addChild(item);

			itemsTable.put(info.getId(), item);
			items.add(item);
		}

		return item;
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);
		ChameleonMenus.constructMenu(this, getModel(), menu);

		if (getFriends().size() > 0) {
			MenuBuilder friends = menu.addSubMenu("Friends");

			friends.addSection("Find more");
			if (getWorldLayer() instanceof SocialGround) {
				SocialGround ground = (SocialGround) getWorldLayer();
				friends.addAction(new ExpandNetworkAction("In current window", 2, ground, this));
			}
			if (!isWindowEnabled()) {
				friends.addAction(new SetWindowEnabled("In new window", true));
			} else {
				friends.addAction(new SetWindowEnabled("Close window", false));
			}
			friends.addSection("Navigate to");

			int count = 0;
			for (Person friend : getFriends()) {
				friends.addAction(new NavigateToFriendAction(Util.truncateString(friend.getName(),
						17), friend));
				if (++count >= MAX_FRIENDS_TO_SHOW_IN_MENU) {
					friends.addLabel("     " + (getFriends().size() - MAX_FRIENDS_TO_SHOW_IN_MENU)
							+ " more...");
					break;
				}

			}
		}

		if (!isPhotosEnabled()) {
			menu.addAction(new SetPhotosEnabledAction("Show photos", true));
		} else {
			menu.addAction(new SetPhotosEnabledAction("Hide photos", false));
		}

	}

	@Override
	protected void constructTooltips(TooltipBuilder builder) {
		super.constructTooltips(builder);

		getModel().constructTooltips(builder);
		builder.addProperty("Friends shown", "" + getFriends().size());
	}

	public Collection<Person> getFriends() {
		return Collections.unmodifiableCollection(friends);
	}

	public String getId() {
		return getModel().getId();
	}

	public PersonItem getItem(String itemId) {
		return itemsTable.get(itemId);

	}

	@Override
	public IUser getModel() {
		return (IUser) super.getModel();
	}

	public Collection<SearchValuePair> getSearchableValues() {
		return getModel().getSearchableValues();
	}

	@Override
	public String getTypeName() {
		return "Flickr User";
	}

	private void init(IUser user) {
		items = new LinkedList<PersonItem>();
		itemsTable = new Hashtable<String, PersonItem>();
		myIcon = new PersonIcon(user.getProfilePictureURL());
		addChild(myIcon);
		myIcon.addPropertyChangeListener(Property.BOUNDS_CHANGED, new Listener() {
			public void propertyChanged(Property event) {
				setBounds(myIcon.getFullBounds());
			}
		});

		setName(user.getDisplayName());

	}

	public boolean isFriend(Person person) {
		return friends.contains(person);
	}

	public boolean isPhotosEnabled() {
		if (myPhotoCollage != null && !myPhotoCollage.isDestroyed()) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean isWindowEnabled() {
		if (windowRef.get() != null && !windowRef.get().isDestroyed()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();
		double itemPositionY = getHeight() + 5;
		for (PersonItem item : items) {

			item.setOffset(new Point2D.Double(0, itemPositionY));

			itemPositionY += item.getHeight() + 5;
		}
	}

	public void setPhotosEnabled(boolean enabled) {
		if (enabled) {
			if (!isPhotosEnabled()) {

				IStreamingPhotoSource flickrPhotos = FlickrPhotoSource.createUserSource(getModel()
						.getId(), true);
				PhotoCollage collage = new PhotoCollage(flickrPhotos);
				myPhotoCollage = collage;
				collage.setScale(0.5f);
				collage.setTransparency(0f);
				addChild(collage);

				Point2D size = collage.localToParent(new Point2D.Double(collage.getWidth(), collage
						.getHeight()));

				collage.setOffset(-size.getX() / 2d, -size.getY() / 2d);
				collage.animateToPosition(getBounds().getMaxX() + 5, collage.getOffset().getY(),
						ChameleonStyle.MEDIUM_ANIMATION_MS);

				UIEnvironment.getInstance().addActivity(
						new Fader(collage, ChameleonStyle.MEDIUM_ANIMATION_MS, 1f));

				collageShadow = new RectangularEdge(this, collage);
				addChild(collageShadow, 0);
				setAnchored(true);

			}
		} else {
			if (myPhotoCollage != null) {
				myPhotoCollage.destroy();
				collageShadow.destroy();
				myPhotoCollage = null;
			}
		}
	}

	/**
	 * @return Viewer Window
	 */
	protected void setWindowEnabled(boolean enabled) {

		if (enabled) {
			if (windowRef.get() == null || windowRef.get().isDestroyed()) {

				ElasticWorld privateWorld = new PersonWorld(getName() + "'s World", getModel());

				Window window = new Window(this, privateWorld);

				getWorld().zoomToObject(window);
				windowRef = new WeakReference<Window>(window);
			} else if (windowRef.get() != null
					&& windowRef.get().getWindowState() == WindowState.MINIMIZED) {
				windowRef.get().restoreSavedWindow();
			}
		} else {
			if (windowRef.get() != null) {
				windowRef.get().destroy();
			}
		}
	}

}

/**
 * A Microcosm world of a person
 * 
 * @author Shu Wu
 */
class PersonWorld extends ElasticWorld {

	public PersonWorld(String name, IUser user) {
		super(name, new SocialGround());

		Person person = getGround().addPerson(user);

		StandardAction expandNetwork = new ExpandNetworkAction("Expanding network", 2,
				(SocialGround) getGround(), person);

		expandNetwork.doAction();
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

	}

	@Override
	public SocialGround getGround() {
		return (SocialGround) super.getGround();
	}

}
