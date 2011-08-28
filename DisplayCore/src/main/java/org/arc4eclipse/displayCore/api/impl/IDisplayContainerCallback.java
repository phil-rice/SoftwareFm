package org.arc4eclipse.displayCore.api.impl;

import org.arc4eclipse.displayCore.api.IDisplayer;
import org.eclipse.swt.widgets.Control;

interface IDisplayContainerCallback {

	<L extends Control, S extends Control> void process(int index, String key, IDisplayer<L, S> displayer, L largeControl, S smallControl) throws Exception;

}
