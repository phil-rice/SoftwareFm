package org.softwarefm.core.friends;

import java.util.List;

/**
 * The problem we have is that the browser is logged in, and the eclipse plugin (i.e. Java code) doesn't know who you are... So we could
 * <ul>
 * <li>ask the user to log in twice (sucks big time),
 * <li>Navigate a browser to pages and rip data from it (also sucks big time)
 * <li>Something else I haven't thought of
 * </ul>
 * So giving a choice between two unpleasant approaches, I've gone for the one that has the best user experience. I suspect that this is seriously error prone...but we will get live and see
 * 
 * @author Phil
 * 
 */
public interface IWikiDataGetter {

	/** Go finds your name. Returns null if cannot find it */
	String myName();

	/** Go finds your friends and calls the callback */
	void myFriends(IWikiGetterCallback<List<FriendData>> callback);
}
