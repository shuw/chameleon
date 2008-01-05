package ca.shu.ui.chameleon.objects;

import ca.shu.ui.chameleon.adapters.IPhoto;

/*
 * 
 * Any class that implements this should be Thread Safe!
 * 
 * 
 */
public interface IStreamingPhotoHolder {
	public boolean addPhoto(IPhoto photo);
	
	public void removePhoto(GPhoto photo);
	
	public void setSourceState(SourceState state);
	
	public enum SourceState {
		ERROR, NORMAL
	}

}
