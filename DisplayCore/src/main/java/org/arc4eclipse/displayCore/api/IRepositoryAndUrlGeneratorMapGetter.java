package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGeneratorMap;

public interface IRepositoryAndUrlGeneratorMapGetter {

	IArc4EclipseRepository getRepository();

	IUrlGeneratorMap getUrlGeneratorMap();
}
