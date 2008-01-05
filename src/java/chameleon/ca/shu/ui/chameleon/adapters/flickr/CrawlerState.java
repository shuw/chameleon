package ca.shu.ui.chameleon.adapters.flickr;

import java.io.Serializable;
import java.util.Date;

public class CrawlerState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5139369998087318031L;

	public Date currentDate;

	public int currentPage;

	public int currentPhotoCounter;

	public String handle;

	public int numPerPage;

	public CrawlerState(String handle, Date currentDate, int currentPage,
			int currentPhotoOnPageNum, int numPerPage) {
		super();
		this.handle = handle;
		this.currentDate = currentDate;
		this.currentPage = currentPage;
		this.currentPhotoCounter = currentPhotoOnPageNum;
		this.numPerPage = numPerPage;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getCurrentPhotoCounter() {
		return currentPhotoCounter;
	}

	public String getHandle() {
		return handle;
	}

	public int getNumPerPage() {
		return numPerPage;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void setCurrentPhotoCounter(int currentPhotoOnPageNum) {
		this.currentPhotoCounter = currentPhotoOnPageNum;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

}
