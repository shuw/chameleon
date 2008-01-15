package ca.shu.ui.chameleon.objects;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.objects.AbstractButton;
import ca.shu.ui.lib.world.piccolo.objects.TextButton;
import ca.shu.ui.lib.world.piccolo.primitives.Image;
import ca.shu.ui.lib.world.piccolo.primitives.Path;

public class PhotoInfoFrame extends WorldObjectImpl {

	private static final long serialVersionUID = 1L;

	IPhoto photoInterface;

	public PhotoInfoFrame(IPhoto photoWr) {
		super();
		this.photoInterface = photoWr;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initialize();
			}
		});
	}

	protected void initialize() {

		Path rectangle = Path.createRectangle(0, 0, 500, 500);
		addChild(rectangle);

		Image profileImage;
		profileImage = new Image(photoInterface.getProfilePicUrl());
		profileImage.translate(5, 5);

		addChild(profileImage);

		createButtons();
	}

	public void createButtons() {
		AbstractButton openSetBtn = new TextButton("Open Set", new Runnable() {
			public void run() {

			}
		});

		this.addChild(openSetBtn);
	}

}
