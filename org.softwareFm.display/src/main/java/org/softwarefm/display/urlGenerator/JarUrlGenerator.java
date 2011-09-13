package org.softwarefm.display.urlGenerator;

import org.softwarefm.display.data.IUrlGenerator;

public class JarUrlGenerator implements IUrlGenerator {

	@Override
	public String findUrlFor(String entity, Object data) {
		return data.toString();
	}

}
