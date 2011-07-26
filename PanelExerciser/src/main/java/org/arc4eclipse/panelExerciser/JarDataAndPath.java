package org.arc4eclipse.panelExerciser;

import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.JarData;
import org.arc4eclipse.utilities.maps.Maps;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class JarDataAndPath {

	public final IJarData data;
	public final Resource jar;

	public JarDataAndPath(String jarPath, Object... namesAndValues) {
		this.data = new JarData(Maps.<String, Object> makeMap(namesAndValues));
		this.jar = new ClassPathResource(jarPath);
	}

}
