package org.softwareFm.display.urlGenerator;

import org.softwareFm.display.data.IUrlGenerator;

public class JarUrlGenerator implements IUrlGenerator {

	@Override
	public String findUrlFor(String entity, Object data) {
		return data.toString();
	}

}
