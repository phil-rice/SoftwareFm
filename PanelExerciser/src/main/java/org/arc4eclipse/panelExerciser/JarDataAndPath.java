package org.arc4eclipse.panelExerciser;

import java.io.File;

import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.JarData;
import org.arc4eclipse.utilities.maps.Maps;

public class JarDataAndPath {

	public final IJarData data;
	public final File jar;

	public JarDataAndPath(String jarPath, Object... namesAndValues) {
		this.data = new JarData(Maps.<String, Object> makeMap(namesAndValues));
		this.jar = new File(jarPath);
	}

}
