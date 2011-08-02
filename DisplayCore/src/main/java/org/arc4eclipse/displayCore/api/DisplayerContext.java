package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.swtBasics.images.IImageFactory;

public class DisplayerContext {
	public final IImageFactory imageFactory;
	public final IArc4EclipseRepository repository;

	public DisplayerContext(IImageFactory imageFactory, IArc4EclipseRepository repository) {
		this.imageFactory = imageFactory;
		this.repository = repository;
	}

}
