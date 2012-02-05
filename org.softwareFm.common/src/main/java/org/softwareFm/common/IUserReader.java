package org.softwareFm.common;

import java.util.Map;

import org.softwareFm.common.internal.LocalUserReader;
import org.softwareFm.common.url.IUrlGenerator;

public interface IUserReader {
	<T> T getUserProperty(Map<String, Object> userDetails, String cryptoKey, String property);

	void refresh(Map<String, Object> userDetails);

	abstract public static class Utils {
		public static IUserReader localUserReader(IGitLocal gitLocal, IUrlGenerator userGenerator) {
			return new LocalUserReader(gitLocal, userGenerator);

		}

	}
}
