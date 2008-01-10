package ca.shu.ui.chameleon.actions;

import ca.shu.ui.chameleon.adapters.IAsyncNetworkLoader;
import ca.shu.ui.chameleon.adapters.INetworkListener;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public abstract class AbstractNetworkAction extends StandardAction implements
		INetworkListener {

	private static final long serialVersionUID = 1L;

	public AbstractNetworkAction(String actionName, int numOfDegrees) {
		super("Open Social Network", actionName, false);

	}

	@Override
	protected final void action() throws ActionException {
		// load friends in new thread
		IAsyncNetworkLoader loader = getNetworkLoader();
		String rootId = getRootId();

		loader.loadNetworkAsync(rootId, 2, this);
	}

	protected abstract IAsyncNetworkLoader getNetworkLoader();

	protected abstract String getRootId() throws ActionException;

}
