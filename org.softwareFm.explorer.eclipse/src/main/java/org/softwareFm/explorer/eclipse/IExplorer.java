package org.softwareFm.explorer.eclipse;

import org.softwareFm.display.composites.IHasComposite;

public interface IExplorer extends IHasComposite {

	/** Switch the LHS of the card to showing a url.*/
	void setCardUrl(String url);

}
