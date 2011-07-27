package org.arc4eclipse.jdtBinding.api;

import org.arc4eclipse.jdtBinding.api.impl.JarDigestor;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.springframework.core.io.Resource;

public interface IJarDigester extends IFunction1<Resource, String> {

	public static class Utils {
		public static IJarDigester digester() {
			return new JarDigestor();
		}
	}

}
