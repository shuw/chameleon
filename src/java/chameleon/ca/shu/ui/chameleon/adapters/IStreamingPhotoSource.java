package ca.shu.ui.chameleon.adapters;

import java.util.Collection;

import ca.shu.ui.chameleon.objects.IStreamingPhotoHolder;

public interface IStreamingPhotoSource {

	public void close();
	public IPhoto getPhoto() throws IPhotoSourceException, SourceEmptyException;
	public Collection<IPhoto> getPhotos(int count) throws IPhotoSourceException, SourceEmptyException;
	public Thread getPhotosAsync(int count, IStreamingPhotoHolder photoHolder);
	
}

