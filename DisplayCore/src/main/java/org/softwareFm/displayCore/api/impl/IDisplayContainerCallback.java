package org.softwareFm.displayCore.api.impl;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.displayCore.api.IDisplayer;

interface IDisplayContainerCallback {

	<L extends Control, S extends Control> void process(int index, String key, IDisplayer<L, S> displayer, L largeControl, S smallControl) throws Exception;

}
