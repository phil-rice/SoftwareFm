package org.softwareFm.displayJavadocAndSource;

import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.core.plugin.SelectedArtifactSelectionManager;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JavaProjects;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class JavadocPanel extends JavadocOrSourcePanel {

	public JavadocPanel(Composite parent, int style, DisplayerContext displayerContext, DisplayerDetails displayerDetails) {
		super(parent, style, displayerContext, displayerDetails, DisplayJavadocConstants.linkKey);
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

	public static void main(String[] args) {
		Swts.display("Javadoc", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = Images.withBasics(from.getDisplay());
				Images.registerImages(from.getDisplay(), imageRegistry, Images.class, SwtBasicConstants.folderKey);
				Images.registerImages(from.getDisplay(), imageRegistry, DisplayJavadocConstants.class, "javadoc.link");
				IResourceGetter resourceGetter = Resources.resourceGetterWithBasics().with(DisplayJavadocConstants.class, "JavadocAndSource");
				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(new GridLayout());
				final String key = "javadoc";

				DisplayerContext displayerContext = DisplayerContext.Utils.forTest(from.getDisplay(), resourceGetter, imageRegistry);
				DisplayerDetails displayerDetails = new DisplayerDetails("anyEntity", Maps.<String, String> makeMap(DisplayCoreConstants.key, key));

				final JavadocPanel panel = new JavadocPanel(composite, SWT.NULL, displayerContext, displayerDetails);
				addButton(composite, panel, "null/null", key, null, null);
				addButton(composite, panel, "SFM/null", key, "SFM", null);
				addButton(composite, panel, "null/Ecl", key, null, "C:\\");
				addButton(composite, panel, "SFM/Ecl", key, "SFM", "C:\\");
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}

			private void addButton(Composite composite, final JavadocOrSourcePanel panel, String label, final String key, final String softwareFmValue, final String eclipseValue) {
				Button state1 = new Button(composite, SWT.PUSH);
				state1.setText(label);
				SelectionAdapter listener = new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Map<String, Object> data = Maps.<String, Object> makeMap("x", eclipseValue, key, softwareFmValue);
						Map<String, Object> context = Maps.<String, Object> newMap();
						panel.setValue(new BindingContext(RepositoryDataItemStatus.FOUND, "url", data, context));
					}

				};
				state1.addSelectionListener(listener);
			}
		});
	}

	@Override
	public void clearEclipseValue() {
		sendToEclipse("");
	}

}
