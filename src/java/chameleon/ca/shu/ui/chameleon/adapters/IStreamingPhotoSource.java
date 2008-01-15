package ca.shu.ui.chameleon.adapters;

import java.util.Collection;

public interface IStreamingPhotoSource {

	public void close();

	public IPhoto getPhoto() throws IStreamingSourceException,
			SourceEmptyException;

	public Collection<IPhoto> getPhotos(int count)
			throws IStreamingSourceException, SourceEmptyException;

	public String getName();

}
