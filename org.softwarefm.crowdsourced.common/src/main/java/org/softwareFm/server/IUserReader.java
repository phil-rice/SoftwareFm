package org.softwareFm.server;

import java.util.Map;

import org.softwareFm.server.internal.LocalUserReader;
import org.softwareFm.utilities.url.IUrlGenerator;

public interface IUserReader {
	<T> T getUserProperty(Map<String, Object> userDetails, String cryptoKey, String property);

	public static class Utils {
		public static IUserReader localUserReader(IGitLocal gitLocal, IUrlGenerator userGenerator) {
			return new LocalUserReader(gitLocal, userGenerator);

		}

	}
}
