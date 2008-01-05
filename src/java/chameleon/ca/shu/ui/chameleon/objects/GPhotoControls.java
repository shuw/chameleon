//package ca.shu.ui.chameleon.objects;
//
//import java.net.URL;
//
//import ca.shu.ui.chameleon.adapters.IPhoto;
//import ca.shu.ui.chameleon.adapters.flickr.FlickrPhoto;
//import ca.shu.ui.lib.Style.Style;
//import ca.shu.ui.lib.objects.PXText;
//import ca.shu.ui.lib.world.WorldObject;
//
//public class GPhotoControls extends WorldObject {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	private GPhoto photo;
//
//	private static final double SCALE_FACTOR = 2;
//
//	private static final long ANIMATE_TIME_MS = 1000;
//
//	public GPhotoControls(GPhoto photo) {
//		super();
//		this.photo = photo;
//
//		addMetadata();
//
//		// this.addInputEventListener(new ControlMouseEventHandler(this));
//	}
//
//	public void addMetadata() {
//		IPhoto photoProxy = photo.getProxy();
//
//		String appendix = "";
//
//		WorldObject metaDataNode = new GTooltip(photoProxy.getTitle()
//				+ appendix);
//
//		FlickrPhoto fPhoto = (FlickrPhoto) photoProxy;
//
//		PXText Details = new PXText("Taken on: " + photoProxy.getDateTaken()
//				+ ", Comments (" + photoProxy.getCommentsCount()
//				+ "), Favorites (" + fPhoto.favorites.size() + "), By: "
//				+ photoProxy.getAuthorName());
//		Details.setFont(Style.GPHOTO_DETAILS_FONT);
//		Details.setWidth(600);
//		Details.recomputeLayout();
//		metaDataNode.addToLayout(Details);
//
//		PXText Decription = new PXText(photoProxy.getDescription());
//		Decription.setWidth(600);
//		Decription.recomputeLayout();
//		metaDataNode.addToLayout(Decription);
//
//		addToLayout(metaDataNode);
//
//	}
//
//	// Method to show a URL
//	public boolean showURL(URL url) {
//		try {
//			// Lookup the javax.jnlp.BasicService object
//			BasicService bs = (BasicService) ServiceManager
//					.lookup("javax.jnlp.BasicService");
//			// Invoke the showDocument method
//			return bs.showDocument(url);
//		} catch (UnavailableServiceException ue) {
//			// Service is not supported
//			return false;
//		}
//	}
//}