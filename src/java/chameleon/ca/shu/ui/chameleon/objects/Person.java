package ca.shu.ui.chameleon.objects;

import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.actions.flickr.ExpandNetworkAction;
import ca.shu.ui.chameleon.adapters.IStreamingPhotoSource;
import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhotoSource;
import ca.shu.ui.chameleon.world.ChameleonStyle;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.piccolo.objects.RectangularEdge;
import ca.shu.ui.lib.world.piccolo.primitives.Image;

public class Person extends ModelObject implements Interactable {

	private static final long serialVersionUID = 1L;

	private RectangularEdge collageShadow = null;

	private PhotoCollage myPhotoCollage = null;

	private Image profileImage;

	public Person(IUser user) {
		super(user);

		(new Thread(new Runnable() {
			public void run() {
				loadProfileImage();
			}
		})).start();

		setName(user.getRealName());
	}

	private void loadProfileImage() {

		profileImage = new Image(getModel().getProfilePictureURL());
		profileImage.setPickable(false);

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
		ChameleonMenus.constructMenu(this, getModel(), menu);

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
	public IUser getModel() {
		return (IUser) super.getModel();
	}

	@Override
	public String getTypeName() {
		return "Flickr User";
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
				collage.setTransparency(0f);
				addChild(collage);

				Point2D size = collage.localToParent(new Point2D.Double(collage
						.getWidth(), collage.getHeight()));

				collage.setOffset(-size.getX() / 2d, -size.getY() / 2d);
				collage
						.animateToPosition(getBounds().getMaxX() + 5, collage
								.getOffset().getY(),
								ChameleonStyle.MEDIUM_ANIMATION_MS);

				addActivity(new Fader(collage,
						ChameleonStyle.MEDIUM_ANIMATION_MS, 1f));

				collageShadow = new RectangularEdge(this, collage);
				addChild(collageShadow, 0);
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
