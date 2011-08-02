package org.arc4eclipse.displayCore.api;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.swtBasics.images.Images;

public class BindingContext {

	public final IArc4EclipseRepository repository;
	public final Images images;
	public final String url;
	public final Map<String, Object> data;
	public final Map<String, Object> context;

	public BindingContext(IArc4EclipseRepository repository, Images images, String url, Map<String, Object> data, Map<String, Object> context) {
		this.repository = repository;
		this.images = images;
		this.url = url;
		this.data = data;
		this.context = context;
	}

}
