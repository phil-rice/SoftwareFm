package org.arc4eclipse.arc4eclipseRepository.api.impl;

import java.io.File;

import org.arc4eclipse.arc4eclipseRepository.api.IJarDigester;
import org.arc4eclipse.utilities.collections.Files;

public class JarDigestor implements IJarDigester {

	@Override
	public String apply(File from) throws Exception {
		return Files.digestAsHexString(from);
	}

}
