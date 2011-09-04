package org.softwareFm.displayCore.api;

import java.util.Map;

import org.softwareFm.repository.api.IRepositoryStatusListener;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.repository.api.IUrlGeneratorMap;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.repository.constants.RepositoryConstants;

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
		ISoftwareFmRepository repository = repositoryAndUrlGeneratorMapGetter.getRepository();
		IUrlGeneratorMap urlGeneratorMap = repositoryAndUrlGeneratorMapGetter.getUrlGeneratorMap();
		Object actualEntity = context.get(RepositoryConstants.entity);
		if (actualEntity.equals(originalEntity))
			if (status == RepositoryDataItemStatus.NOT_FOUND)
				repository.notifyListenersThereIsNoData(dependantEntity, ISoftwareFmRepository.Utils.makeSecondaryNotFoundContext(dependantEntity));
			else if (item != null) {
				String reportedUrl = (String) item.get(urlKey);
				if (reportedUrl == null)
					repository.notifyListenersThereIsNoData(dependantEntity, ISoftwareFmRepository.Utils.makeSecondaryNotFoundContext(dependantEntity));
				else {
					IUrlGenerator urlGenerator = urlGeneratorMap.get(dependantEntity);
					String actualUrl = urlGenerator.apply(reportedUrl);
					repository.getData(dependantEntity, actualUrl, ISoftwareFmRepository.Utils.makeSecondaryContext(dependantEntity, urlKey, reportedUrl));
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
