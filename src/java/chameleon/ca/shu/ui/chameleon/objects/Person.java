package ca.shu.ui.chameleon.objects;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.actions.flickr.ExpandNetworkAction;
import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.RectangularEdge;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;

import com.aetrion.flickr.people.User;

import edu.umd.cs.piccolo.nodes.PImage;

public class Person extends ModelObject implements IUser, Interactable {

	private static final long serialVersionUID = 1L;

	private RectangularEdge collageShadow = null;

	private PhotoCollage myPhotoCollage = null;

	private PImage profileImage;

	public Person(User user) {
		super(user);

		(new Thread(new Runnable() {
			public void run() {
				loadProfileImage();
			}
		})).start();

		setName(user.getRealName());
	}

	private void loadProfileImage() {

		try {
			profileImage = new PImage(new URL(getModel().getBuddyIconUrl()));
			profileImage.setPickable(false);

		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage());
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				profileImage.setOffset(-profileImage.getWidth() / 2f,
						-profileImage.getWidth() / 2f);
				addChild(profileImage);
				setBounds(parentToLocal(getFullBounds()));
				// setBounds(profileImage.localToParent(profileImage.getBounds()));
				profileImage.setPaint(Style.COLOR_DISABLED);
			}
		});

	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);
		ChameleonMenus.constructMenu(this, menu);

		if (getWorldLayer() instanceof SocialGround) {
			SocialGround ground = (SocialGround) getWorldLayer();
			menu.addAction(new ExpandNetworkAction("Show friends", 2, ground,
					this));
		}

		if (!isPhotosEnabled()) {
			menu.addAction(new SetPhotosEnabledAction("Show photos", true));
		} else {
			menu.addAction(new SetPhotosEnabledAction("Hide photos", true));
		}
	}

	public String getId() {
		return getModel().getId();
	}

	@Override
	public User getModel() {
		return (User) super.getModel();
	}

	public URL getProfilePictureURL() {
		try {
			return new URL(getModel().getBuddyIconUrl());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getRealName() {
		return getModel().getRealName();
	}

	@Override
	public String getTypeName() {
		return "Flickr User";
	}

	public String getUserName() {
		return getModel().getUsername();
	}

	public boolean isPhotosEnabled() {
		if (myPhotoCollage != null) {
			return true;
		} else {
			return false;
		}
	}

	public void setPhotosEnabled(boolean enabled) {
		if (enabled) {
			if (myPhotoCollage == null) {

				IStreamingPhotoSource flickrPhotos = FlickrPhotoSource
						.createUserSource(getModel().getId(), true);
				PhotoCollage collage = new PhotoCollage(flickrPhotos);
				collage.setScale(0.5f);
				addChild(collage);
				collageShadow = new RectangularEdge(this, collage);
				addChild(0, collageShadow);
			}
		} else {
			if (myPhotoCollage != null) {
				myPhotoCollage.destroy();
				collageShadow.destroy();
				myPhotoCollage = null;
			}
		}
	}

	class SetPhotosEnabledAction extends StandardAction {

		private static final long serialVersionUID = 1L;
		private boolean enabled;

		public SetPhotosEnabledAction(String description, boolean enabled) {
			super("Open photos");
			this.enabled = enabled;
		}

		@Override
		protected void action() throws ActionException {
			setPhotosEnabled(enabled);
		}

	}
}
