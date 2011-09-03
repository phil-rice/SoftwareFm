package org.softwareFm.jdtBinding.api;

import org.softwareFm.jdtBinding.api.impl.JarDigestor;
import org.softwareFm.utilities.functions.IFunction1;
import org.springframework.core.io.Resource;

public interface IJarDigester extends IFunction1<Resource, String> {

	public static class Utils {
		public static IJarDigester digester() {
			return new JarDigestor();
		}
	}

}
