package ca.shu.ui.chameleon.flickr.adapters;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.primitives.Image;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

public class PersonIcon extends WorldObjectImpl {

	private URL profilePicUrl;
	private Image profileImage;

	public PersonIcon(URL profilePicUrl) {
		super();
		this.profilePicUrl = profilePicUrl;
		init();
	}

	private void init() {
		loadingImageText = new Text("loading...");
		addChild(loadingImageText);
		(new Thread(new Runnable() {
			public void run() {
				try {
					loadProfileImage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		})).start();
	}

	private Text loadingImageText;

	private void loadProfileImage() throws InterruptedException, InvocationTargetException {

		profileImage = new Image(profilePicUrl);
		profileImage.setPickable(false);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadingImageText.destroy();
				addChild(profileImage);
				setBounds(parentToLocal(getFullBounds()));
				profileImage.setPaint(Style.COLOR_DISABLED);
			}
		});

	}

}
