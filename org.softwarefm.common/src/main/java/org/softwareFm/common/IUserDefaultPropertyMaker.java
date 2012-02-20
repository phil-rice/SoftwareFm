package org.softwareFm.common;

/** Used when the local needs a property that hasn't yet received it's default value */
public interface IUserDefaultPropertyMaker {
	void makeProperty(String softwareFmId, String userCrypto, String propertyName);
}
