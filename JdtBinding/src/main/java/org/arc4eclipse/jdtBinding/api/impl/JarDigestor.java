package org.arc4eclipse.jdtBinding.api.impl;

import org.arc4eclipse.jdtBinding.api.IJarDigester;
import org.arc4eclipse.utilities.collections.Files;
import org.springframework.core.io.Resource;

public class JarDigestor implements IJarDigester {

	@Override
	public String apply(Resource from) throws Exception {
		return Files.digestAsHexString(from);
	}

}
