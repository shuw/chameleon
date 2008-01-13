package ca.shu.ui.chameleon.objects;

import javax.swing.SwingUtilities;

import ca.shu.ui.chameleon.adapters.IPhoto;
import ca.shu.ui.lib.objects.AbstractButton;
import ca.shu.ui.lib.objects.TextButton;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

public class PhotoInfoFrame extends WorldObject {

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

		PPath rectangle = PPath.createRectangle(0, 0, 500, 500);
		this.addChild(rectangle);

		PImage profileImage;
		profileImage = new PImage(photoInterface.getProfilePicUrl());
		profileImage.translate(5, 5);

		this.addChild(profileImage);

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
