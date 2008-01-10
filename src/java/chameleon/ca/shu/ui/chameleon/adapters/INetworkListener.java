package ca.shu.ui.chameleon.adapters;

public interface INetworkListener {

	public void acceptNewConnection(String userAId, String userBId, int degreesFromRoot);
}
