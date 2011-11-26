/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.arc4eclipse.panelExerciser;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.maps.Maps;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class JarDataAndPath {

	public final Map<String, Object> data;
	public final Resource jar;

	public JarDataAndPath(String jarPath, Class<?> anchor, Object... namesAndValues) {
		this.data = Maps.<String, Object> makeMap(namesAndValues);
		this.jar = new ClassPathResource(jarPath, anchor);
		try {
			Assert.assertTrue(jar.getFile().exists());
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public String toString() {
		return "JarDataAndPath [data=" + data + ", jar=" + jar + "]";
	}

}