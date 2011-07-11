package org.arc4eclipse.repositoryClient.paths.impl;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IJarDetails;
import org.arc4eclipse.repositoryClient.constants.RepositoryClientConstants;
import org.arc4eclipse.repositoryClient.paths.IPathCalculator;
import org.arc4eclipse.repositoryClient.paths.IPathCalculatorThin;

@SuppressWarnings("rawtypes")
public final class ClassAndMethodPathCalculator implements IPathCalculator<Object, IEntityType> {
	private final IPathCalculatorThin thin;
	private final IJarDetails jarDetails;

	public ClassAndMethodPathCalculator(IPathCalculatorThin thin, IJarDetails jarDetails) {
		this.thin = thin;
		this.jarDetails = jarDetails;
	}

	public String makeUrl(Object thing, IEntityType entityType) {
		Class clazz = getClassFor(thing);
		switch (entityType) {
		case PROJECT:
			return thin.getProjectPath(jarDetails);
		case RELEASE:
			return thin.getReleasePath(jarDetails);
		case PACKAGE:

			return thin.getPackagePath(jarDetails, clazz.getPackage().getName());
		case CLASS:
			return thin.getClassPath(jarDetails, clazz.getPackage().getName(), clazz.getSimpleName());// inadequate...but will do for now
		case METHOD:
			Method method = getMethodFor(thing);
			return thin.getMethodPath(jarDetails, clazz.getPackage().getName(), clazz.getSimpleName(), method.getName());// inadequate...but will do for now
		default:
			break;
		}
		return null;
	}

	private Method getMethodFor(Object thing) {
		if (thing == null)
			throw new NullPointerException();
		else if (thing instanceof Method)
			return (Method) thing;
		else
			throw new IllegalArgumentException(MessageFormat.format(RepositoryClientConstants.notAMethod, thing, thing.getClass()));
	}

	private Class getClassFor(Object thing) {
		if (thing == null)
			throw new NullPointerException();
		else if (thing instanceof Class)
			return (Class) thing;
		else if (thing instanceof Method)
			return ((Method) thing).getDeclaringClass();
		else
			throw new IllegalArgumentException(MessageFormat.format(RepositoryClientConstants.notAClassOrMethod, thing, thing.getClass()));
	}
}