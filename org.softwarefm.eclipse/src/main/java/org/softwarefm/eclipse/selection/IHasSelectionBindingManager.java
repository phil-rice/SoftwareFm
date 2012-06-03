package org.softwarefm.eclipse.selection;


public interface IHasSelectionBindingManager  {
	<S> ISelectedBindingManager<S> getBindingManager();
}
