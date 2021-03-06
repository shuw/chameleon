package ca.shu.ui.chameleon.actions;

import ca.shu.ui.chameleon.adapters.IAsyncNetworkLoader;
import ca.shu.ui.chameleon.adapters.INetworkListener;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public abstract class NetworkAction extends StandardAction implements
		INetworkListener {

	private static final long serialVersionUID = 1L;
	private int numOfDegrees;

	public NetworkAction(String actionName, int numOfDegrees) {
		super("Open Social Network", actionName, false);
		this.numOfDegrees = numOfDegrees;

	}

	@Override
	protected final void action() throws ActionException {
		// load friends in new thread
		IAsyncNetworkLoader loader = getNetworkLoader();
		Person root = getPersonRoot();

		loader.loadNetworkAsync(root, numOfDegrees, this);
	}

	protected abstract IAsyncNetworkLoader getNetworkLoader();

	protected abstract Person getPersonRoot() throws ActionException;

}
