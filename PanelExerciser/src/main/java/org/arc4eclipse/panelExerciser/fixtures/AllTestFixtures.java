package org.arc4eclipse.panelExerciser.fixtures;

import org.arc4eclipse.utilities.reflection.Fields;
import org.springframework.core.io.ClassPathResource;

public class AllTestFixtures {

	public static <T> Iterable<T> allConstants(Class<T> t) {
		return Fields.constantsOfClass(new ClassPathResource(".", AllTestFixtures.class), AllTestFixtures.class.getPackage().getName(), t);
	}

}
