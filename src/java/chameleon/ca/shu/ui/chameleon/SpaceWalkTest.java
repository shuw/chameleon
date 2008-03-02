package ca.shu.ui.chameleon;

public class SpaceWalkTest {
	public static void main(String[] args) throws Exception {
		msra_rankbase02.spacewalkv4.WebService webservice = new msra_rankbase02.spacewalkv4.WebService();
		msra_rankbase02.spacewalkv4.WebServiceSoap spaceWalk = webservice
				.getWebServiceSoap();

		String returned = spaceWalk.initWhatsNewItems("shushuwu");
		System.out.println(returned);
		
	}
}
