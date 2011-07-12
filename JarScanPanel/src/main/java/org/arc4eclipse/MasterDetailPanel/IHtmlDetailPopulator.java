package org.arc4eclipse.MasterDetailPanel;

import org.arc4eclipse.utilities.callbacks.ICallback;

/** Turn the main object into some HTML, and call the callback with the result */
public interface IHtmlDetailPopulator<T> {

	void populateWith(T mainObject, ICallback<String> response);
}
