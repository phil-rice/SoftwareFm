package org.softwareFm.utilities.annotations;

/**
 * This is a marker. It is used to say that the method it is attached to is at the heart of tight loop. Think before changing it: Don't use iterator if you can help it: use a for (int i = 0; i<size(); i++) loop instead.
 */
public @interface TightLoop {

}
