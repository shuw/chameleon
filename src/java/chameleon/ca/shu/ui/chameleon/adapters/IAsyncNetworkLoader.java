package ca.shu.ui.chameleon.adapters;

public interface IAsyncNetworkLoader {

	public void close();

	public void loadNetworkAsync(String userIdRoot, int degrees,
			INetworkListener networkListener);

}
