package org.arc4eclipse.displayCore.api.impl;

import org.arc4eclipse.displayCore.api.NameSpaceAndName;

public interface ITopButtonState {

	boolean state(NameSpaceAndName nameSpaceAndName);

	boolean toogleState(NameSpaceAndName nameSpaceAndName);
}
