package ca.shu.ui.chameleon.world;

import java.util.Collection;

import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.elastic.ElasticWorld;

/**
 * @author Shu Wu
 */
public class WorldX extends ElasticWorld {

	public WorldX() {
		super("Chameleon", new SocialGround());
	}

	@Override
	protected void constructSelectionMenu(Collection<WorldObject> selection,
			PopupMenuBuilder menu) {
		super.constructSelectionMenu(selection, menu);
		menu.addAction(new GroupObjectsAction("New Window", selection));
	}

	class GroupObjectsAction extends StandardAction {
		private Collection<WorldObject> objects;

		public GroupObjectsAction(String description,
				Collection<WorldObject> objects) {
			super(description);
			this.objects = objects;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {
			Ensemble group = new Ensemble(objects);

			getGround().addChild(group);
			group.setEnsembleVisible(true);
		}

	}

}
