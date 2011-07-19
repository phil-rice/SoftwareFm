package org.arc4eclipse.swtBinding.basic;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.utilities.functions.IFunction1;

public class BindingContext<Data> {

	public Class<Data> clazz;
	public final IArc4EclipseRepository repository;
	public IFunction1<Map<String, Object>, Data> mapper;

	public BindingContext(Class<Data> clazz, IArc4EclipseRepository repository, IFunction1<Map<String, Object>, Data> mapper) {
		this.clazz = clazz;
		this.repository = repository;
		this.mapper = mapper;
	}

}
