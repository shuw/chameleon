package ca.shu.ui.chameleon.objects;

import ca.shu.ui.chameleon.adapters.IChameleonObj;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;
import ca.shu.ui.lib.world.IWorldObject;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class ChameleonMenus {
	private static final long SCALE_DURATION_MS = 500;

	private static final double SCALE_FACTOR = 1.5;

	protected static void constructMenu(IWorldObject worldObj,
			AbstractMenuBuilder menu) {
		constructMenu(worldObj, null, menu);
	}

	protected static void constructMenu(IWorldObject worldObj,
			IChameleonObj chamObj, AbstractMenuBuilder menu) {
		ChameleonMenus chameleonMenu = new ChameleonMenus(worldObj, chamObj);
		chameleonMenu.constructMenu(menu);
	}

	private IChameleonObj chameleonObj;
	private IWorldObject worldObj;

	public ChameleonMenus(IWorldObject worldObj, IChameleonObj chameleonObj) {
		super();
		this.worldObj = worldObj;
		this.chameleonObj = chameleonObj;
	}

	protected void constructMenu(AbstractMenuBuilder menu) {
		AbstractMenuBuilder objectMenu = menu.addSubMenu("Object");
		objectMenu.addAction(new ChangeSizeAction("Grow", true));
		objectMenu.addAction(new ChangeSizeAction("Shrink", false));

		if (chameleonObj != null) {
			menu.addAction(new OpenURL("Open in browser", chameleonObj));
		}
	}

	class ChangeSizeAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		private boolean increase;

		public ChangeSizeAction(String description, boolean increase) {
			super(description);
			this.increase = increase;
		}

		@Override
		protected void action() throws ActionException {
			double scale = worldObj.getScale();
			if (increase) {
				scale *= SCALE_FACTOR;
			} else {
				scale /= SCALE_FACTOR;
			}
			worldObj.animateToPositionScaleRotation(
					worldObj.getOffset().getX(), worldObj.getOffset().getY(),
					scale, worldObj.getRotation(), SCALE_DURATION_MS);

		}
	}

}

class OpenURL extends StandardAction {

	private static final long serialVersionUID = 1L;

	IChameleonObj chamObj;

	public OpenURL(String description, IChameleonObj chamObj) {
		super(description);
		this.chamObj = chamObj;
	}

	@Override
	protected void action() throws ActionException {
		BrowserLauncher browserLauncher;
		try {
			browserLauncher = new BrowserLauncher(null);
			browserLauncher.openURLinBrowser(chamObj.getUrl().toString());
		} catch (BrowserLaunchingInitializingException e) {
			e.printStackTrace();
		} catch (UnsupportedOperatingSystemException e) {
			e.printStackTrace();
		} catch (BrowserLaunchingExecutionException e) {
			e.printStackTrace();
		}
	}

}
