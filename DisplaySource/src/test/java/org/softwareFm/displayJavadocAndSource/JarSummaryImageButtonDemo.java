package org.softwareFm.displayJavadocAndSource;

import java.util.Collections;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.softwareFmImages.IImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class JarSummaryImageButtonDemo {

	public static void main(String[] args) {
		Swts.display("JarSummaryImageButtonDemo", new IFunction1<Composite, Composite>() {

			private IResourceGetter resourceGetter;
			private Set<SmallIconPosition> filter;
			private JarSummaryImageButton jarSummaryImageButton;
			private ImageRegistry imageRegistry;
			private Composite parent;

			@Override
			public Composite apply(Composite from) throws Exception {
				parent = new Composite(from, SWT.NULL);
				imageRegistry = IImageRegisterConfigurator.Utils.withBasics(from.getDisplay());
				resourceGetter = Resources.resourceGetterWithBasics("org.softwareFm.displayJavadocAndSource.JavadocAndSource");
				filter = SmallIconPosition.allIcons;
				DisplayerContext context = DisplayerContext.Utils.forTest(from, resourceGetter, imageRegistry);
				DisplayerDetails displayerDetails = new DisplayerDetails("entity", Maps.<String, String> makeMap(DisplayCoreConstants.key, "jar", DisplayCoreConstants.smallImageKey, ArtifactsAnchor.jarKey));
				jarSummaryImageButton = new JarSummaryImageButton(parent, context, displayerDetails, true, filter);

				makeFilterButton("None", Collections.<SmallIconPosition> emptySet());
				makeFilterButton("JavadocOnly", SmallIconPosition.allTop);
				makeFilterButton("SourceOnly", SmallIconPosition.allBottom);
				makeFilterButton("All", SmallIconPosition.allIcons);

				boolean[] falseTrue = new boolean[] { false, true };
				new Label(parent, SWT.NULL).setText("EclJD, RepJD, EclSrc, RepSrc");
				for (boolean eclipseJavadoc : falseTrue)
					for (boolean repositoryJavadoc : falseTrue) {
						Composite buttonPanel = new Composite(parent, SWT.NULL);
						buttonPanel.setLayout(Swts.getHorizonalNoMarginRowLayout());
						for (boolean eclipseSource : falseTrue)
							for (boolean repositorySource : falseTrue)
								makeStateButton(buttonPanel, eclipseJavadoc, repositoryJavadoc, eclipseSource, repositorySource);
					}

				Swts.addGrabHorizontalAndFillGridDataToAllChildren(parent);

				SourceAndJavadocState emptyState = new SourceAndJavadocState(resourceGetter);
				jarSummaryImageButton.setSourceAndJavadocState(emptyState);
				return parent;
			}

			private void makeFilterButton(String name, final Set<SmallIconPosition> newfilter) {
				Button button = new Button(parent, SWT.PUSH);
				button.setText(name);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						jarSummaryImageButton.setFilter(newfilter);
					}
				});

			}

			private String l(boolean b) {
				return b ? "T" : "F";
			}

			private void makeStateButton(Composite buttonPanel, final boolean eclipseJavadoc, final boolean repositoryJavadoc, final boolean eclipseSource, final boolean repositorySource) {
				String name = l(eclipseJavadoc) + "/" + l(repositoryJavadoc) + "/" + l(eclipseSource) + "/" + l(repositorySource);
				Button button = new Button(buttonPanel, SWT.PUSH);
				button.setText(name);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						String repositorySourceText = repositorySource ? "RepSrc" : null;
						String repositoryJavadocText = repositoryJavadoc ? "RepJD" : null;
						String eclipseSourceText = eclipseSource ? "EclSrc" : null;
						String eclipseJavadocText = eclipseJavadoc ? "EclJD" : null;
						SourceAndJavadocState state = jarSummaryImageButton.getSourceAndJavadocState().with(eclipseJavadocText, repositoryJavadocText, eclipseSourceText, repositorySourceText);
						jarSummaryImageButton.setSourceAndJavadocState(state);
					}
				});
			}
		});
	}
}
