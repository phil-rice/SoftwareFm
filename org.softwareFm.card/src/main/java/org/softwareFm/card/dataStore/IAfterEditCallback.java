package org.softwareFm.card.dataStore;

public interface IAfterEditCallback {

	/** Once data has been stored on the server, and the server has responded, this is called. The url is where the server has just stored the data */
	void afterEdit(String url);
	public static class  Utils{
		public static MemoryAfterEditCallback memory(){
			return new MemoryAfterEditCallback();
		}
	}
}
