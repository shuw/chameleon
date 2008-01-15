package ca.shu.ui.chameleon.objects;


/*
 * 
 * Any class that implements this should be Thread Safe!
 * 
 * 
 */
public interface IStreamingPhotoHolder {

	public void setSourceState(SourceState state);

	public enum SourceState {
		ERROR, NORMAL
	}

}
