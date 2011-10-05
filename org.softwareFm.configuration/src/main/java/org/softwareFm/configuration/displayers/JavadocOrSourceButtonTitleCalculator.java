package org.softwareFm.configuration.displayers;

import org.softwareFm.utilities.functions.IFunction1;

public class JavadocOrSourceButtonTitleCalculator implements IFunction1<JavadocOrSourceState, TitleAndRunnable> {

	private final Runnable copyToEclipse;
	private final Runnable copyToSoftwareFm;

	public JavadocOrSourceButtonTitleCalculator(Runnable copyToEclipse, Runnable copyToSoftwareFm) {
		this.copyToEclipse = copyToEclipse;
		this.copyToSoftwareFm = copyToSoftwareFm;
	}

	@Override
	public TitleAndRunnable apply(JavadocOrSourceState from) throws Exception {
		if (from.hasEclipse)
			if (from.hasSoftwareFm)
				if (from.eclipseValue.equals(from.softwareFmValue))
					return new TitleAndRunnable("button.matches.title", null);
				else
					return new TitleAndRunnable("button.doesntMatches.title", null);
			else if (from.eclipseValueIsHttp)
				return new TitleAndRunnable("button.copyToSoftwareFm.title", copyToSoftwareFm);
			else
				return new TitleAndRunnable("button.eclipseNotUrl.title", null);
		else
			if (from.hasSoftwareFm)
			return new TitleAndRunnable("button.copyToEclipse.title", copyToEclipse);
			else
				return new TitleAndRunnable("button.noData.title", null);
	}

}
