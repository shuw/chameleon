package ca.shu.ui.chameleon.adapters;

import ca.shu.ui.chameleon.objects.Person;

public interface IAsyncNetworkLoader {

	public void close();

	public void loadNetworkAsync(Person root, int degrees,
			INetworkListener networkListener);

}
