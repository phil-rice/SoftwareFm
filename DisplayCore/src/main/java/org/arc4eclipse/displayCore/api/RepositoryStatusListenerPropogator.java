package org.arc4eclipse.displayCore.api;

import java.text.MessageFormat;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IRepositoryStatusListener;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGeneratorMap;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;

public abstract class RepositoryStatusListenerPropogator implements IRepositoryStatusListener {

	private final String originalEntity;
	private final String dependantEntity;
	private final String urlKey;
	private IRepositoryAndUrlGeneratorMapGetter repositoryAndUrlGeneratorMapGetter;

	public RepositoryStatusListenerPropogator(String originalEntity, String dependantEntity, String urlKey) {
		super();
		this.originalEntity = originalEntity;
		this.dependantEntity = dependantEntity;
		this.urlKey = urlKey;
	}

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item, Map<String, Object> context) throws Exception {
		IArc4EclipseRepository repository = repositoryAndUrlGeneratorMapGetter.getRepository();
		IUrlGeneratorMap urlGeneratorMap = repositoryAndUrlGeneratorMapGetter.getUrlGeneratorMap();
		Object actualEntity = context.get(RepositoryConstants.entity);
		if (status == RepositoryDataItemStatus.FOUND)
			if (actualEntity.equals(originalEntity))
				if (item != null) {
					IUrlGenerator urlGenerator = urlGeneratorMap.get(dependantEntity);
					String reportedUrl = (String) item.get(urlKey);
					if (reportedUrl == null)
						throw new NullPointerException(MessageFormat.format(DisplayCoreConstants.missingValueInMap, urlKey, item));
					String actualUrl = urlGenerator.apply(reportedUrl);
					repository.getData(dependantEntity, actualUrl, IArc4EclipseRepository.Utils.makeSecondaryContext(dependantEntity, urlKey, reportedUrl));
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
