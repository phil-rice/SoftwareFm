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
