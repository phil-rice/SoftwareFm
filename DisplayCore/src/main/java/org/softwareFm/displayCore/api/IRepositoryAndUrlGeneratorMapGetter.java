package org.softwareFm.displayCore.api;

import org.softwareFm.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.softwareFm.arc4eclipseRepository.api.IUrlGeneratorMap;

public interface IRepositoryAndUrlGeneratorMapGetter {

	IArc4EclipseRepository getRepository();

	IUrlGeneratorMap getUrlGeneratorMap();
}
