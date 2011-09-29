package org.softwareFm.display.swt;

import org.softwareFm.display.composites.IHasControl;

public interface ISituationListListener <T extends IHasControl>{

	public void selected(T hasControl, String selectedItem) throws Exception;
}
