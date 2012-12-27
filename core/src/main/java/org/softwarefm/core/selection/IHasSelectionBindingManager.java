package org.softwarefm.core.selection;


public interface IHasSelectionBindingManager  {
	<S> ISelectedBindingManager<S> getBindingManager();
}
