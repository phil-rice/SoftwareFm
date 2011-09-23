package org.softwareFm.configuration.displayers;

import java.text.MessageFormat;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.ButtonDisplayer;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.runnable.Runnables;
import org.softwareFm.utilities.strings.Strings;

public class JavadocOrSourceButtonDisplayer extends ButtonDisplayer {

	protected Runnable runnable;
	private final String artifact;

	public JavadocOrSourceButtonDisplayer(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey, String artifact) {
		super(config, parent, titleOrTitleKey, titleIsKey);
		this.artifact = artifact;
		runnable = Runnables.noRunnable;
	}

	@Override
	public void data(final IDataGetter dataGetter, DisplayerDefn defn, String entity) {
		final String eclipseValue = Strings.nullSafeToString(dataGetter.getDataFor("data.raw.jar." + artifact));
		String repositoryValue = Strings.nullSafeToString(dataGetter.getDataFor("data.jar." + artifact));
		String tooltip = MessageFormat.format(dataGetter.getDataFor("button.javadocSource.tooltip").toString(), eclipseValue, repositoryValue);
		button.setToolTipText(tooltip);
		if (eclipseValue.equals(""))
			if (repositoryValue.equals(""))
				setButton(dataGetter, false, Runnables.noRunnable, "button.noData.title");
			else
				setButton(dataGetter, true, new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						try {
							ICallback<String> callback = (ICallback<String>) dataGetter.getDataFor("data.raw.jar." + artifact + "Mutator");
							callback.process(eclipseValue);
						} catch (Exception e) {
							throw WrappedException.wrap(e);
						}
					}
				}, "button.copyToEclipse.title");
		else if (repositoryValue.equals(""))
			if (eclipseValue.startsWith("http:"))
				setButton(dataGetter, true, new Runnable() {
					@Override
					public void run() {
					}
				}, "button.copyToSoftwareFm.title");
			else
				setButton(dataGetter, false, Runnables.noRunnable, "button.eclipseNotUrl.title");
		else if (eclipseValue.equals(repositoryValue))
			setButton(dataGetter, false, Runnables.noRunnable, "button.matches.title");
		else
			setButton(dataGetter, false, Runnables.noRunnable, "button.doesntMatches.title");

	}

	private void setButton(IDataGetter dataGetter, boolean enabled, Runnable runnable, String title) {
		button.setEnabled(enabled);
		this.runnable = runnable;
		button.setText(Strings.nullSafeToString(dataGetter.getDataFor(title)));
	}

	@Override
	protected void buttonPressed() {
	};

}
