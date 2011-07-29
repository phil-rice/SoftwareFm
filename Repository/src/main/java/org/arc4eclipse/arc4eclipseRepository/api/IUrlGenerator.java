package org.arc4eclipse.arc4eclipseRepository.api;

import org.arc4eclipse.utilities.functions.IFunction1;

public interface IUrlGenerator {

	IFunction1<String, String> forJar();

	IFunction1<String, String> forOrganisation();

	IFunction1<String, String> forProject();

}
