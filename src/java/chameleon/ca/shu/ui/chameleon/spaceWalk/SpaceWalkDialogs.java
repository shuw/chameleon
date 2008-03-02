package ca.shu.ui.chameleon.spaceWalk;

import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.UserMessages.DialogException;

public class SpaceWalkDialogs {

	public static String askUserAlias() throws DialogException {
		return UserMessages
				.askDialog("Please enter a Space alias (<alias>.spaces.live.com)");
	}

}
