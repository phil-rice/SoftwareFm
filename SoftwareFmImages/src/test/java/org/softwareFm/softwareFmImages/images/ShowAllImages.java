package org.softwareFm.softwareFmImages.images;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.softwareFmImages.IImageRegister;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class ShowAllImages {

	interface Acceptor {

		boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm);
	}

	public static void main(String[] args) {
		Swts.display("Images", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = IImageRegister.Utils.withBasics(from.getDisplay());
				Composite composite = new Composite(from, SWT.NULL);
				Composite jarComposite = new Composite(composite, SWT.BORDER);

				addJarVariants(imageRegistry, jarComposite, "jar", new Acceptor() {
					@Override
					public boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm) {
						return true;
					}
				});
				addJarVariants(imageRegistry, jarComposite, "jarClearEclipse", new Acceptor() {
					@Override
					public boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm) {
						return source;
					}
				});
				addJarVariants(imageRegistry, jarComposite, "jarCopyFromSoftwareFm", new Acceptor() {
					@Override
					public boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm) {
						return !source && sourceFm;
					}
				});
				addJarVariants(imageRegistry, jarComposite, "jarCopyToSoftwareFm", new Acceptor() {
					@Override
					public boolean accept(boolean source, boolean javadoc, boolean sourceFm, boolean javadocFm) {
						return source && !sourceFm;
					}
				});

				Composite artifactsComposite = new Composite(composite, SWT.BORDER);
				crossProductArtifactsAndOverlays(imageRegistry, artifactsComposite);

				Swts.addGrabHorizontalAndFillGridDataToAllChildren(jarComposite);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(artifactsComposite);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);

				return composite;
			}

			boolean[] falseTrue = new boolean[] { false, true };

			private void addJarVariants(ImageRegistry imageRegistry, Composite composite, String key, Acceptor acceptor) {
				Composite rowComposite = new Composite(composite, SWT.NULL);
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
											map.put(SmallIconPosition.TopLeft, "javadoc");
										if (source)
											map.put(SmallIconPosition.BottomLeft, "source");
										if (javadocFm)
											map.put(SmallIconPosition.TopRight, "softwareFm");
										if (sourceFm)
											map.put(SmallIconPosition.BottomRight, "softwareFm");
										ImageButton button = makeJarVarient(rowComposite, imageRegistry, key, map);
										button.setState(state);
									}
			}

			private void addTitle(Composite rowComposite, String string) {
				Label label = new Label(rowComposite, SWT.NULL);
				label.setText(string);
				label.setLayoutData(new RowData(150, SWT.DEFAULT));
			}

			private ImageButton makeJarVarient(Composite rowComposite, ImageRegistry imageRegistry, String key, Map<SmallIconPosition, String> map) {
				ImageButton button = new ImageButton(rowComposite, imageRegistry, "artifact." + key, false);
				for (Entry<SmallIconPosition, String> entry : map.entrySet()) {
					button.setSmallIcon(entry.getKey(), "smallIcon." + entry.getValue());
				}
				return button;
			}

			private void crossProductArtifactsAndOverlays(ImageRegistry imageRegistry, Composite composite) {
				for (String name : Images.getNamesFor(ArtifactsAnchor.class, "jar.png")) {
					if (name.startsWith("jar") && name.length() > 3)
						continue;
					Composite rowComposite = new Composite(composite, SWT.NULL);
					rowComposite.setLayout(Swts.getHorizonalNoMarginRowLayout());
					addTitle(rowComposite, name);
					String artifactKey = "artifact." + name;
					for (boolean state : falseTrue)
						makeOneRow(imageRegistry, rowComposite, artifactKey, state);
				}
			}

			private void makeOneRow(ImageRegistry imageRegistry, Composite rowComposite, String artifactKey, boolean state) {
				new ImageButton(rowComposite, imageRegistry, artifactKey, false).setState(state);

				for (String overlay : Images.getNamesFor(OverlaysAnchor.class, "add.png"))
					new ImageButton(rowComposite, imageRegistry, artifactKey, "overlay." + overlay, false).setState(state);
			}

		});
	}

}
