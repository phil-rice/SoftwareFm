package org.softwareFm.display.swt;

import org.softwareFm.display.composites.IHasControl;

public interface IHasControlWithSelectedItemListener extends IHasControl {
	void itemSelected(String item) throws Exception;

}
