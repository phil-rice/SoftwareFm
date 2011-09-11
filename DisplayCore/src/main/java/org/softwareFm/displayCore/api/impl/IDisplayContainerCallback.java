package org.softwareFm.displayCore.api.impl;

import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.swtBasics.IHasControl;

interface IDisplayContainerCallback {

	<L extends IHasControl, S extends IHasControl> void process(int index, String key, IDisplayer<L, S> displayer, L largeControl, S smallControl) throws Exception;

}
