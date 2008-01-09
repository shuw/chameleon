package ca.shu.ui.chameleon.objects;

import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;
import ca.shu.ui.lib.world.elastic.ElasticObject;

public class ChameleonMenus {
	private static final long SCALE_DURATION_MS = 500;

	private static final double SCALE_FACTOR = 1.5;

	protected static void constructMenu(ElasticObject chameleonObj,
			AbstractMenuBuilder menu) {
		ChameleonMenus chameleonMenu = new ChameleonMenus(chameleonObj);
		chameleonMenu.constructMenu(menu);
	}

	private ElasticObject chameleonObj;

	public ChameleonMenus(ElasticObject chameleonObj) {
		super();
		this.chameleonObj = chameleonObj;
	}
	protected void constructMenu(AbstractMenuBuilder menu) {
		AbstractMenuBuilder objectMenu = menu.addSubMenu("Object");
		objectMenu.addAction(new ChangeSizeAction("Grow", true));
		objectMenu.addAction(new ChangeSizeAction("Shrink", false));
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
			double scale = chameleonObj.getScale();
			if (increase) {
				scale *= SCALE_FACTOR;
			} else {
				scale /= SCALE_FACTOR;
			}
			chameleonObj.animateToPositionScaleRotation(chameleonObj.getOffset().getX(),
					chameleonObj.getOffset().getY(), scale, chameleonObj.getRotation(),
					SCALE_DURATION_MS);

		}
	}
}
