package ca.shu.ui.chameleon.adapters;

import java.util.Collection;

import ca.shu.ui.chameleon.adapters.flickr.CrawlerState;
import ca.shu.ui.chameleon.adapters.flickr.FlickrPhoto;

public interface IStreamingPhotoSource {

	public void close();

	public IPhoto getPhoto() throws IStreamingSourceException, SourceEmptyException;

	public CrawlerState getState();

	public Collection<FlickrPhoto> getPhotos(int count) throws IStreamingSourceException,
			SourceEmptyException;

	public String getName();

}
