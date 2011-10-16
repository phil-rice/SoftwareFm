package org.softwareFm.display.swt;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.IHasControl;

public interface ISituationListAndBuilder<T extends IHasControl> {
	void selected(T hasControl, String context, Object value) throws Exception;

	T makeChild(Composite parent) throws Exception;

}
