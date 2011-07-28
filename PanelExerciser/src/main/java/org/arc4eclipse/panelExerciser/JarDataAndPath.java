package org.arc4eclipse.panelExerciser;

import java.util.Map;

import org.arc4eclipse.utilities.maps.Maps;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class JarDataAndPath {

	public final Map<String, Object> data;
	public final Resource jar;

	public JarDataAndPath(String jarPath, Class<?> anchor, Object... namesAndValues) {
		this.data = Maps.<String, Object> makeMap(namesAndValues);
		this.jar = new ClassPathResource(jarPath, anchor);
	}

	@Override
	public String toString() {
		return "JarDataAndPath [data=" + data + ", jar=" + jar + "]";
	}

}
