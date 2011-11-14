package org.softwareFm.card.editors;

import org.softwareFm.display.composites.IHasControl;

public interface IOkCancel extends IHasControl {
	void setOkEnabled(boolean enabled);

	boolean isOkEnabled();

	void ok();

	void cancel();
}
