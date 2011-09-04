package org.softwareFm.displayCore.api;

import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.IUrlGeneratorMap;

public interface IRepositoryAndUrlGeneratorMapGetter {

	ISoftwareFmRepository getRepository();

	IUrlGeneratorMap getUrlGeneratorMap();
}
