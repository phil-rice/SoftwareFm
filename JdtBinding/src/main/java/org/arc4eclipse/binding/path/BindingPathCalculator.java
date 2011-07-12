package org.arc4eclipse.binding.path;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IJarDetails;
import org.arc4eclipse.repositoryClient.paths.IPathCalculator;
import org.arc4eclipse.repositoryClient.paths.IPathCalculatorThin;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;

public final class BindingPathCalculator implements IPathCalculator<IBinding, IEntityType> {
	private final IPathCalculatorThin thin;
	private final JavaElementRipper ripper = new JavaElementRipper();

	private final Map<IPath, IJarDetails> cache = Maps.newMap();

	public BindingPathCalculator(IPathCalculatorThin thin) {
		this.thin = thin;
	}

	@Override
	public String makeUrl(IBinding thing, IEntityType entityType) {
		final IPath path = thing.getJavaElement().getPath();
		if (path == null)
			return null;
		IJarDetails jarDetails = Maps.findOrCreate(cache, path, new Callable<IJarDetails>() {

			@Override
			public IJarDetails call() throws Exception {
				String lastSegment = path.lastSegment();
				return IJarDetails.Utils.makeJarDetails(new File(lastSegment), "SomeRelease");
			}
		});
		JavaElementRipperResult ripperResult = ripper.apply(thing);
		String packageName = ripperResult.packageName;
		String className = ripperResult.className;
		String methodName = ripperResult.methodName;
		switch (entityType) {
		case PROJECT:
			return thin.getProjectPath(jarDetails);
		case RELEASE:
			return thin.getReleasePath(jarDetails);
		case PACKAGE:
			if (packageName == null)
				return null;
			return thin.getPackagePath(jarDetails, packageName);
		case CLASS:
			if (packageName == null || className == null)
				return null;
			return thin.getClassPath(jarDetails, packageName, className);// inadequate...but will do for now
		case METHOD:
			if (packageName == null || className == null || methodName == null)
				return null;
			return thin.getMethodPath(jarDetails, packageName, className, methodName);// inadequate...but will do for now
		default:
			return null;
		}
	}

}