package ca.shu.ui.chameleon.spaceWalk.objects;

import java.net.URL;

import ca.shu.ui.chameleon.adapters.IPersonItemInfo;
import ca.shu.ui.chameleon.spaceWalk.actions.FindRelatedItemsAction;
import ca.shu.ui.chameleon.spaces.objects.SpaceUser;

public class BlogItemInfo implements IPersonItemInfo {
	String contents;
	String id;
	String title;
	URL url;
	String sessionId;

	public BlogItemInfo(String sessionId, String id, String title, String contents, URL url) {
		super();
		this.sessionId = sessionId;
		this.contents = contents;
		this.id = id;
		this.url = url;
		this.title = title;
	}

	public String getContents() {
		return contents;
	}

	public String getId() {
		return id;
	}

	public URL getURL() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public void findRelatedItems(UIContext context) {
		(new FindRelatedItemsAction("Find Related Items", context.getGround(), (SpaceUser) context
				.getUserParent(), this)).doAction();

	}

	public String getSessionId() {
		return sessionId;
	}

}
