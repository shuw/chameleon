package ca.shu.ui.chameleon.adapters;

import ca.shu.ui.chameleon.objects.PersonItem;
import ca.shu.ui.chameleon.world.SocialGround;

public interface IPersonItemInfo extends IChameleonObj {

	public String getId();

	public String getContents();

	public String getTitle();

	public void findRelatedItems(UIContext context);

	public static class UIContext {
		SocialGround ground;
		IUser userParent;
		PersonItem uiParent;


		public UIContext(SocialGround ground, IUser userParent,
				PersonItem uiParent) {
			super();
			this.ground = ground;
			this.userParent = userParent;
			this.uiParent = uiParent;
		}

		public SocialGround getGround() {
			return ground;
		}

		public PersonItem getUIParent() {
			return uiParent;
		}

		public IUser getUserParent() {
			return userParent;
		}

	}

}
