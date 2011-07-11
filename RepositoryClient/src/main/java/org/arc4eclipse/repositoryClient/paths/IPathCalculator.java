package org.arc4eclipse.repositoryClient.paths;

import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IJarDetails;
import org.arc4eclipse.repositoryClient.paths.impl.ClassAndMethodPathCalculator;
import org.arc4eclipse.repositoryClient.paths.impl.JarPathToRepositoryPathConvertor;
import org.arc4eclipse.repositoryClient.paths.impl.PathCalculatorThin;

public interface IPathCalculator<Thing, Aspect> {
	String makeUrl(Thing thing, Aspect entityType);

	public static class Utils {
		public static IPathCalculator<Object, IEntityType> classPathCalculator(IJarDetails jarDetails) {
			JarPathToRepositoryPathConvertor convertor = new JarPathToRepositoryPathConvertor();
			return classPathCalculator(convertor, jarDetails);

		}

		private static IPathCalculator<Object, IEntityType> classPathCalculator(JarPathToRepositoryPathConvertor convertor, IJarDetails jarDetails) {
			return new ClassAndMethodPathCalculator(new PathCalculatorThin(convertor), jarDetails);
		}

	}
}
