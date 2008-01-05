package ca.shu.ui.chameleon.adapters.flickr;

import java.net.MalformedURLException;
import java.net.URL;

import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.chameleon.objects.PhotoCollage;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;

import com.aetrion.flickr.people.User;

import edu.umd.cs.piccolo.nodes.PImage;

public class FlickrUser extends ModelObject implements IUser, Interactable {

	private static final long serialVersionUID = 1L;

	private PImage profileImage;

	public FlickrUser(User user) {
		super(user);
		setChildrenPickable(false);
		try {
			profileImage = new PImage(new URL(user.getBuddyIconUrl()));
			profileImage.setOffset(-profileImage.getWidth() / 2f, -profileImage
					.getWidth() / 2f);

		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage());
		}

		addChild(profileImage);
		setBounds(getFullBounds());
		profileImage.setPaint(Style.COLOR_DISABLED);

	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		menu.addAction(new OpenPhotosAction());
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

	public void openPhotos() {
		IStreamingPhotoSource flickrPhotos = FlickrPhotoSource
				.createUserSource(getModel().getUsername());
		PhotoCollage collage = new PhotoCollage(flickrPhotos);
		addChild(collage);
	}

	class OpenPhotosAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public OpenPhotosAction() {
			super("Open photos");
		}

		@Override
		protected void action() throws ActionException {
			openPhotos();
		}

	}
}
