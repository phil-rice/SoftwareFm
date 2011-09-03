package org.softwareFm.displayCore.api;

import java.util.Map;

import org.softwareFm.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.softwareFm.arc4eclipseRepository.api.IRepositoryStatusListener;
import org.softwareFm.arc4eclipseRepository.api.IUrlGenerator;
import org.softwareFm.arc4eclipseRepository.api.IUrlGeneratorMap;
import org.softwareFm.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;

public abstract class RepositoryStatusListenerPropogator implements IRepositoryStatusListener {

	private final String originalEntity;
	private final String dependantEntity;
	private final String urlKey;
	private IRepositoryAndUrlGeneratorMapGetter repositoryAndUrlGeneratorMapGetter;

	public RepositoryStatusListenerPropogator(String originalEntity, String dependantEntity, String urlKey) {
		this.originalEntity = originalEntity;
		this.dependantEntity = dependantEntity;
		this.urlKey = urlKey;
	}

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item, Map<String, Object> context) throws Exception {
		IArc4EclipseRepository repository = repositoryAndUrlGeneratorMapGetter.getRepository();
		IUrlGeneratorMap urlGeneratorMap = repositoryAndUrlGeneratorMapGetter.getUrlGeneratorMap();
		Object actualEntity = context.get(RepositoryConstants.entity);
		if (actualEntity.equals(originalEntity))
			if (status == RepositoryDataItemStatus.NOT_FOUND)
				repository.notifyListenersThereIsNoData(dependantEntity, IArc4EclipseRepository.Utils.makeSecondaryNotFoundContext(dependantEntity));
			else if (item != null) {
				String reportedUrl = (String) item.get(urlKey);
				if (reportedUrl == null)
					repository.notifyListenersThereIsNoData(dependantEntity, IArc4EclipseRepository.Utils.makeSecondaryNotFoundContext(dependantEntity));
				else {
					IUrlGenerator urlGenerator = urlGeneratorMap.get(dependantEntity);
					String actualUrl = urlGenerator.apply(reportedUrl);
					repository.getData(dependantEntity, actualUrl, IArc4EclipseRepository.Utils.makeSecondaryContext(dependantEntity, urlKey, reportedUrl));
				}
			}
	}

	public void setRepositoryAndUrlGeneratorMapGetter(IRepositoryAndUrlGeneratorMapGetter repositoryAndUrlGeneratorMapGetter) {
		this.repositoryAndUrlGeneratorMapGetter = repositoryAndUrlGeneratorMapGetter;
	}

	public String getOriginalEntity() {
		return originalEntity;
	}

	public String getDependantEntity() {
		return dependantEntity;
	}

	public String getUrlKey() {
		return urlKey;
	}

}
