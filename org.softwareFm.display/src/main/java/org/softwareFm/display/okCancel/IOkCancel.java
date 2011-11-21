package org.softwareFm.display.okCancel;

import org.softwareFm.display.composites.IHasControl;

/** Represents the ok cancel buttons in an editor */
public interface IOkCancel extends IHasControl {
	void setOkEnabled(boolean enabled);

	boolean isOkEnabled();

	/** as though the user has clicked ok */
	void ok();

	/** as though the user has clicked cancel */
	void cancel();
}
