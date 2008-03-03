package ca.shu.ui.chameleon.spaces;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ca.shu.ui.chameleon.exceptions.RescourceDoesNotExist;
import ca.shu.ui.chameleon.spaces.objects.SpaceUser;

public class SpacesAPI {
	private static DocumentBuilder builder;

	private static void initalize() {
		if (builder == null) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			try {
				builder = builderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

	private static Document getResponse(String urlStr) throws IOException,
			SAXException {
		initalize();
		// SAXBuilder parser = new SAXBuilder();
		//
		// Document doc = parser
		// .build("http:// developerlife.com/xmljavatutorial1/AddressBook.xml");
		// NodeList nodes = doc.g

		URL url = new URL(urlStr);

		URLConnection conn = url.openConnection();
		// conn.setRequestMethod("GET");

		conn.connect();

		InputStream in = null;
		try {

			in = conn.getInputStream();

			Document document = builder.parse(in);

			return document;
		} finally {
			in.close();
		}
	}

	public static SpaceUser getSpaceUser(String alias)
			throws RescourceDoesNotExist {
		try {
			Document doc = getResponse("http://" + alias
					+ ".spaces.live.com/profile/feed.rss");

			Element rssElement = doc.getDocumentElement();
			Element channelElement = (Element) rssElement.getFirstChild();

			Element imageNode = getChildElement(channelElement, "image");
			URL profileURL = new URL(getChildValue(channelElement, "link"));

			URL profilePictureUrl = new URL(getChildValue(imageNode, "url"));

			// String alias = getChildValue(identity, "live:alias");

			return new SpaceUser(alias, profilePictureUrl, profileURL);
		} catch (IOException e) {
			// e.printStackTrace();
			throw new RescourceDoesNotExist();
		} catch (SAXException e) {
			// e.printStackTrace();
			throw new RescourceDoesNotExist();
		}
	}

	public static Element getChildElement(Element parentEl, String childName) {
		return (Element) parentEl.getElementsByTagName("image").item(0);
	}

	public static String getChildValue(Element el, String name) {
		Node childELement = el.getElementsByTagName(name).item(0);
		if (childELement != null) {
			Node nodeValue = childELement.getFirstChild();
			if (nodeValue != null) {
				return nodeValue.getNodeValue();
			}
		}
		return "";
	}

	public static void main(String[] args) {
		try {
			SpaceUser profile = getSpaceUser("mike");
			System.out.println("Profile display name: "
					+ profile.getDisplayName());
			System.out.println("Profile pic url: "
					+ profile.getProfilePictureURL().toString());
			System.out.println("Profile url: " + profile.getURL().toString());
		} catch (RescourceDoesNotExist e) {
			e.printStackTrace();
		}

	}
}
