package ca.shu.ui.chameleon.objects;

import javax.swing.JPopupMenu;

import ca.neo.ui.models.tooltips.Tooltip;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.chameleon.adapters.IPersonItemInfo;
import ca.shu.ui.chameleon.world.SocialGround;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

public class PersonItem extends Text implements Interactable {

	private final IPersonItemInfo info;

	public PersonItem(IPersonItemInfo info) {
		super(Util.truncateString(info.getTitle(), 80));
		this.info = info;

		setPickable(true);
		setSelectable(true);
		setDraggable(false);

	}

	public String getId() {
		return info.getId();
	}

	@Override
	public WorldObject getTooltip() {

		TooltipBuilder tooltipBuilder = new TooltipBuilder(getName());
		tooltipBuilder.addProperty("Contents", Util.truncateString(info.getContents(), 400));

		return new Tooltip(tooltipBuilder);

	}

	public JPopupMenu getContextMenu() {

		PopupMenuBuilder menu = new PopupMenuBuilder("Item");
		ChameleonMenus.constructMenu(this, info, menu);

		menu.addAction(new FindRelatedItemsAction("Find related items"));
		return menu.toJPopupMenu();

	}

	class FindRelatedItemsAction extends StandardAction {

		public FindRelatedItemsAction(String description) {
			super(description);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {

			IPersonItemInfo.UIContext context = new IPersonItemInfo.UIContext(
					(SocialGround) getWorldLayer(), ((Person) getParent()).getModel(),
					PersonItem.this);
			info.findRelatedItems(context);
		}

	}

}
