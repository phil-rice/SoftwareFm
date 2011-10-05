package org.softwareFm.configuration.displayers;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.ButtonDisplayer;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.runnable.Runnables;
import org.softwareFm.utilities.strings.Strings;

public class JavadocOrSourceButtonDisplayer extends ButtonDisplayer {

	protected Runnable runnable;
	private final String artifactEclipseKey;
	private final String artifactSoftwareFmKey;
	private final String eclipseMutatorKey;
	private final String artifactKey;

	public JavadocOrSourceButtonDisplayer(CompositeConfig config, Composite parent, String titleOrTitleKey, boolean titleIsKey, String artifactKey, String artifactEclipseKey, String artifactSoftwareFmKey, String eclipseMutatorKey) {
		super(config, parent, titleOrTitleKey, titleIsKey);
		this.artifactKey = artifactKey;
		this.artifactEclipseKey = artifactEclipseKey;
		this.artifactSoftwareFmKey = artifactSoftwareFmKey;
		this.eclipseMutatorKey = eclipseMutatorKey;
		runnable = Runnables.noRunnable;
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runnable.run();
			}
		});	}

	@Override
	public void data(final ActionContext actionContext, DisplayerDefn defn, String entity) {
		final IDataGetter dataGetter = actionContext.dataGetter;
		final String eclipseValue = Strings.nullSafeToString(dataGetter.getDataFor(artifactEclipseKey));
		final String repositoryValue = Strings.nullSafeToString(dataGetter.getDataFor(artifactSoftwareFmKey));
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
							ICallback<String> callback = (ICallback<String>) dataGetter.getDataFor(eclipseMutatorKey);
							callback.process(repositoryValue);
							Map<String,Object> rawData = (Map<String, Object>) dataGetter.getLastRawData("jar");
							rawData.put(artifactKey,  repositoryValue);
							dataGetter.setRawData("jar", rawData);
							
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
						ActionData actionData = dataGetter.getActionDataFor(Collections.<String>emptyList());
						actionContext.updateStore.update(actionData, "data.jar.javadoc", eclipseValue);
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
		String text = Strings.nullSafeToString(dataGetter.getDataFor(title));
		button.setText(text);
	}

	@Override
	protected void buttonPressed() {
	};

}
