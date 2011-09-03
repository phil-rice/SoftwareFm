package org.softwareFm.jdtBinding.api.impl;

import org.softwareFm.jdtBinding.api.IJarDigester;
import org.softwareFm.utilities.collections.Files;
import org.springframework.core.io.Resource;

public class JarDigestor implements IJarDigester {

	@Override
	public String apply(Resource from) throws Exception {
		return Files.digestAsHexString(from);
	}

}
