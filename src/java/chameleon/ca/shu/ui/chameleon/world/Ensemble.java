package ca.shu.ui.chameleon.world;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldLayer;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.elastic.ElasticObject;
import ca.shu.ui.lib.world.piccolo.objects.Window;
import ca.shu.ui.lib.world.piccolo.objects.Window.WindowState;
import ca.shu.ui.lib.world.piccolo.primitives.Image;

/**
 * This is a ensemble of WorldObjects
 * 
 * @author Shu Wu
 */
public class Ensemble extends ElasticObject implements Interactable {
	private WeakReference<Window> windowRef = new WeakReference<Window>(null);

	Collection<WeakReference<IWorldObject>> objectsRef;

	public Ensemble(Collection<IWorldObject> objects) {
		super();
		this.addChild(new Image("images/chameleonIcons/Group.gif"));
		setBounds(parentToLocal(getFullBounds()));

		this.objectsRef = new ArrayList<WeakReference<IWorldObject>>(objects.size());

		for (IWorldObject wo : objects) {
			objectsRef.add(new WeakReference<IWorldObject>(wo));
		}

	}

	private void moveObjectsToWorld(IWorld world) {
		IWorldLayer ground = world.getGround();

		double lowestX = Double.MAX_VALUE;
		double lowestY = Double.MAX_VALUE;
		for (WeakReference<IWorldObject> woRef : objectsRef) {
			if (woRef.get() != null && !woRef.get().isDestroyed()) {
				IWorldObject wo = woRef.get();

				double x = wo.getOffset().getX();
				double y = wo.getOffset().getY();

				if (x < lowestX) {
					lowestX = x;
				}
				if (y < lowestY) {
					lowestY = y;
				}
			}
		}
		// Zero object positions
		for (WeakReference<IWorldObject> woRef : objectsRef) {
			if (woRef.get() != null && !woRef.get().isDestroyed()) {
				IWorldObject wo = woRef.get();

				wo.removeFromWorld();
				wo.translate(-lowestX, -lowestY);
				ground.addChild(wo);
			}
		}
		world.zoomToFit();
	}

	@Override
	protected void prepareForDestroy() {
		setEnsembleVisible(false);
		super.prepareForDestroy();
	}

	public void constructMenu(PopupMenuBuilder menu) {
		menu.addAction(new CollapseAction("Collapse"));
		if (!isEnsembleVisible()) {
			menu.addAction(new SetEnsembleVisibleAction("Show Ensemble", true));
		} else {
			menu.addAction(new SetEnsembleVisibleAction("Hide Ensemble", false));
		}

	}

	@Override
	public void doubleClicked() {
		setEnsembleVisible(true);
	}

	public final JPopupMenu getContextMenu() {
		PopupMenuBuilder menuBuidler = new PopupMenuBuilder("Ensemble of objects");
		constructMenu(menuBuidler);

		return menuBuidler.toJPopupMenu();
	}

	public boolean isEnsembleVisible() {
		if (windowRef.get() != null && !windowRef.get().isDestroyed()
				&& windowRef.get().getWindowState() != WindowState.MINIMIZED) {
			return true;
		} else {
			return false;
		}
	}

	public void setEnsembleVisible(boolean enabled) {
		if (enabled) {
			if (windowRef.get() == null || windowRef.get().isDestroyed()) {

				WorldX chameleon = new WorldX();
				Window window = new Window(this, chameleon);

				moveObjectsToWorld(chameleon);

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

	class CollapseAction extends StandardAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CollapseAction(String description) {
			super(description);
		}

		@Override
		protected void action() throws ActionException {
			moveObjectsToWorld(getWorld());
			destroy();
		}

	}

	class SetEnsembleVisibleAction extends StandardAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private boolean visible;

		public SetEnsembleVisibleAction(String description, boolean visible) {
			super(description);
			this.visible = visible;
		}

		@Override
		protected void action() throws ActionException {
			setEnsembleVisible(visible);
		}

	}
}
