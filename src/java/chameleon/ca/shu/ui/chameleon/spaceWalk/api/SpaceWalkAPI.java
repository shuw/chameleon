package ca.shu.ui.chameleon.spaceWalk.api;

import msra_rankbase02.spacewalkv4.WebServiceSoap;
import ca.shu.ui.chameleon.exceptions.RescourceDoesNotExist;

public class SpaceWalkAPI {

	private SpaceWalkAPI() {

	}

	protected static WebServiceSoap createService() {
		msra_rankbase02.spacewalkv4.WebService webservice = new msra_rankbase02.spacewalkv4.WebService();
		return webservice.getWebServiceSoap();
	}

	public static WhatsNewSession getWhatsNewSession(String spaceAlias)
			throws RescourceDoesNotExist {
		String sessionId = createService().initWhatsNewItems(spaceAlias);

		if (sessionId == null || "".equals(sessionId)) {
			throw new RescourceDoesNotExist("");
		} else {
			return new WhatsNewSession(sessionId);
		}

	}

	public static void main(String[] args) throws Exception {
		WhatsNewSession whatsNewSession = new WhatsNewSession("mike");
		

	}
}
