package org.softwareFm.displayJavadocAndSource;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.core.plugin.SelectedArtifactSelectionManager;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JavaProjects;
import org.softwareFm.softwareFmImages.smallIcons.SmallIconsAnchor;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.utilities.exceptions.WrappedException;

public class JavadocPanel extends JavadocOrSourcePanel {

	public JavadocPanel(Composite parent, int style, DisplayerContext displayerContext, DisplayerDetails displayerDetails) {
		super(parent, style, displayerContext, displayerDetails, JavadocSourceConstants.linkKey);
	}

	@Override
	public void sendToEclipse(String value) {
		BindingRipperResult ripped = (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
		BindingRipperResult uptoDate = SelectedArtifactSelectionManager.reRip(ripped);
		if (uptoDate != null)
			JavaProjects.setJavadoc(uptoDate.javaProject, uptoDate.classpathEntry, value);
	}

	@Override
	protected String findEclipseValue(BindingContext bindingContext) throws Exception {
		if (bindingContext != null) {
			Map<String, Object> context = bindingContext.context;
			BindingRipperResult ripped = (BindingRipperResult) context.get(DisplayCoreConstants.ripperResult);
			BindingRipperResult updated = SelectedArtifactSelectionManager.reRip(ripped);
			String javadoc = updated == null ? null : JavaProjects.findJavadocFor(updated.classpathEntry);
			return javadoc;
		}
		return null;
	}

	@Override
	protected void openEclipseValue(String eclipseValue) {
		browseOrOpenFile(eclipseValue);
	}

	@Override
	public void clearEclipseValue() {
		sendToEclipse("");
	}

	@Override
	protected String getEclipseSmallIconKey() {
		return SmallIconsAnchor.javadocKey;
	}

	@Override
	protected SmallIconPosition getLeftPosition() {
		return SmallIconPosition.TopLeft;
	}

	@Override
	protected SmallIconPosition getRightPosition() {
		return SmallIconPosition.TopRight;
	}

	public static void main(String[] args) {
		final PanelMakerForTest panelMaker = new PanelMakerForTest() {

			@Override
			public JavadocOrSourcePanel make(Composite parent, DisplayerContext displayerContext, DisplayerDetails displayerDetails) {
				final JavadocPanel panel = new JavadocPanel(parent, SWT.NULL, displayerContext, displayerDetails) {
					@Override
					protected String findEclipseValue(BindingContext bindingContext) throws Exception {
						return (String) bindingContext.data.get("x");
					}

					@Override
					public void sendToEclipse(String value) {
						System.out.println("Going to eclipse: " + value);
					}

					@Override
					public void sendToRepository(String value) {
						System.out.println("Going to repository: " + value);
					}

					@Override
					protected void openEclipseValue(String eclipseValue) {
						try {
							Desktop.getDesktop().open(new File(eclipseValue));
						} catch (IOException e) {
							throw WrappedException.wrap(e);
						}
					}

					@Override
					public void clearEclipseValue() {
						System.out.println("Clearing eclipse value");
					}

				};
				return panel;
			}
		};
		show(panelMaker);
	}
}
