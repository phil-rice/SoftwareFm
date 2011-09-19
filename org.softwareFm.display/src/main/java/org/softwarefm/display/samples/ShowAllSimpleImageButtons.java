package org.softwarefm.display.samples;

import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.softwareFmImages.IImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.smallButtons.ImageButtonConfig;
import org.softwarefm.display.smallButtons.SimpleImageButton;

public class ShowAllSimpleImageButtons {

	interface Acceptor {

		boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm);
	}

	public static void main(String[] args) {
		Swts.display("Images", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = IImageRegisterConfigurator.Utils.withBasics(from.getDisplay());
				ImageButtonConfig imageButtonConfig = ImageButtonConfig.forTests(imageRegistry);
				Composite composite = new Composite(from, SWT.NULL);
				Composite jarComposite = new Composite(composite, SWT.BORDER);

				addJarVariants(imageButtonConfig, jarComposite, "jar", new Acceptor() {
					@Override
					public boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm) {
						return true;
					}
				});
				addJarVariants(imageButtonConfig, jarComposite, "jarClearEclipse", new Acceptor() {
					@Override
					public boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm) {
						return source;
					}
				});
				addJarVariants(imageButtonConfig, jarComposite, "jarCopyFromSoftwareFm", new Acceptor() {
					@Override
					public boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm) {
						return !source && sourceFm;
					}
				});
				addJarVariants(imageButtonConfig, jarComposite, "jarCopyToSoftwareFm", new Acceptor() {
					@Override
					public boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm) {
						return source && !sourceFm;
					}
				});

				Composite artifactsComposite = new Composite(composite, SWT.BORDER);
				crossProductArtifactsAndOverlays(artifactsComposite, imageButtonConfig);

				Swts.addGrabHorizontalAndFillGridDataToAllChildren(jarComposite);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(artifactsComposite);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);

				return composite;
			}

			boolean[] falseTrue = new boolean[] { false, true };

			private void addJarVariants(final ImageButtonConfig imageButtonConfig, Composite composite, String key, Acceptor acceptor) {
				final Composite rowComposite = new Composite(composite, SWT.NULL);
				IButtonParent buttonParent = IButtonParent.Utils.buttonParent(rowComposite, imageButtonConfig.imageRegistry, IResourceGetter.Utils.noResources());
				rowComposite.setLayout(Swts.getHorizonalNoMarginRowLayout());
				addTitle(rowComposite, key);
				for (boolean state : falseTrue)
					for (boolean source : falseTrue)
						for (boolean javadoc : falseTrue)
							for (boolean sourceFm : falseTrue)
								for (boolean javadocFm : falseTrue)
									if (acceptor.accept(source, javadoc, sourceFm, javadocFm)) {
										Map<SmallIconPosition, String> map = Maps.newMap();
										if (javadoc)
											map.put(SmallIconPosition.TopLeft, "smallIcon.javadoc");
										if (source)
											map.put(SmallIconPosition.BottomLeft, "smallIcon.source");
										if (javadocFm)
											map.put(SmallIconPosition.TopRight, "smallIcon.softwareFm");
										if (sourceFm)
											map.put(SmallIconPosition.BottomRight, "smallIcon.softwareFm");
										SimpleImageButton button = makeJarVarient(buttonParent, imageButtonConfig.withImage("artifact." + key), map, state);

									}
			}

			private void addTitle(Composite rowComposite, String string) {
				Label label = new Label(rowComposite, SWT.NULL);
				label.setText(string);
				label.setLayoutData(new RowData(150, SWT.DEFAULT));
			}

			private SimpleImageButton makeJarVarient(IButtonParent buttonParent, ImageButtonConfig config, Map<SmallIconPosition, String> map, boolean state) {
				SimpleImageButton button = new SimpleImageButton(buttonParent, config);
				button.setSmallIconMap(map);
				button.getControl().setLayoutData(new RowData(config.layout.smallButtonWidth, config.layout.smallButtonHeight));
				button.setValue(state);
				return button;
			}

			private void crossProductArtifactsAndOverlays(Composite composite, ImageButtonConfig config) {
				ImageRegistry imageRegistry = config.imageRegistry;
				for (String name : Images.getNamesFor(ArtifactsAnchor.class, "jar.png")) {
					if (name.startsWith("jar") && name.length() > 3)
						continue;
					Composite rowComposite = new Composite(composite, SWT.NULL);
					rowComposite.setLayout(Swts.getHorizonalNoMarginRowLayout());
					addTitle(rowComposite, name);
					String artifactKey = "artifact." + name;
					IButtonParent buttonParent = IButtonParent.Utils.buttonParent(rowComposite, imageRegistry, IResourceGetter.Utils.noResources());
					for (boolean state : falseTrue)
						makeOneRow(buttonParent, config, artifactKey, state);
				}
			}

			private void makeOneRow(IButtonParent buttonParent, ImageButtonConfig config, String artifactKey, boolean state) {
				SimpleImageButton button = new SimpleImageButton(buttonParent, config.withImage(artifactKey));
				button.setValue(state);
				button.getControl().setLayoutData(new RowData(config.layout.smallButtonWidth, config.layout.smallButtonHeight));
				for (String overlay : Images.getNamesFor(OverlaysAnchor.class, "add.png")) {
					SimpleImageButton overlayButton = new SimpleImageButton(buttonParent, config.withImage(artifactKey, "overlay." + overlay));
					overlayButton.setValue(state);
					overlayButton.getControl().setLayoutData(new RowData(config.layout.smallButtonWidth, config.layout.smallButtonHeight));
					
				}
			}

		});
	}
}
