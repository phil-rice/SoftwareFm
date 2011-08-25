package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.swtBasics.IHasComposite;

/** This represents the data about an entity */
public interface IDisplayContainer extends IHasComposite, IStatusChangedListener {

	/** The display manager sends this component some data to be to displayed */
	void setValues(BindingContext bindingContext);

}
