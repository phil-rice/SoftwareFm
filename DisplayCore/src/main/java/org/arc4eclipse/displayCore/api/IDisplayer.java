package org.arc4eclipse.displayCore.api;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IDisplayer {

	String getNameSpace();

	Control makeCompositeAsChildOf(ITitleLookup titleLookup, Composite parent, NameSpaceNameAndValue nameSpaceNameAndValue);

}
