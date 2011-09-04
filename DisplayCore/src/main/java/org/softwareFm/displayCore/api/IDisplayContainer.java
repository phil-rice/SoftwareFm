package org.softwareFm.displayCore.api;

import org.softwareFm.repository.api.IRepositoryStatusListener;
import org.softwareFm.swtBasics.IHasComposite;

/** This represents the data about an entity */
public interface IDisplayContainer extends IHasComposite, IRepositoryStatusListener {

	/** The display manager sends this component some data to be to displayed */
	void setValues(BindingContext bindingContext);

}
