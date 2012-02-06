package org.softwareFm.common;

import org.softwareFm.common.internal.LocalUserReader;
import org.softwareFm.common.url.IUrlGenerator;

public interface IUserReader {
	<T> T getUserProperty(String userId, String cryptoKey, String property);

	void refresh(String userId);

	abstract public static class Utils {
		public static IUserReader localUserReader(IGitLocal gitLocal, IUrlGenerator userGenerator) {
			return new LocalUserReader(gitLocal, userGenerator);

		}

	}
}
