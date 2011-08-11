package org.arc4eclipse.displayCore.api.impl;

import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.eclipse.swt.widgets.Control;

interface IDisplayContainerCallback {

	<L extends Control, S extends Control> void process(NameSpaceAndName nameSpaceAndName, IDisplayer<L, S> displayer) throws Exception;

}
