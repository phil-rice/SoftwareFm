package org.arc4eclipse.panelExerciser;

import java.io.File;

import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IReleaseData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.JarData;
import org.arc4eclipse.utilities.maps.Maps;

public class JarDataAndPath {

	public final IJarData data;
	public final File jar;

	public JarDataAndPath(IReleaseData data, String jarPath) {
		this.data = new JarData(Maps.fromSimpleMap(data));
		this.jar = new File(jarPath);
	}

}
