package org.softwareFm.swtBasics.images;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.artifacts.ArtifactsAnchor;
import org.softwareFm.swtBasics.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.smallIcons.SmallIconsAnchor;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.springframework.core.io.ClassPathResource;

public class ShowAllImages {

	public static void main(String[] args) {
		Swts.display("Images", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = new ImageRegistry(from.getDisplay());
				registerImages(from, imageRegistry, ArtifactsAnchor.class, "jar", "artifact");
				registerImages(from, imageRegistry, OverlaysAnchor.class, "add", "overlay");
				registerImages(from, imageRegistry, SmallIconsAnchor.class, "javadoc", "smallIcon");
				Composite composite = new Composite(from, SWT.NULL);

				addJarVariants(imageRegistry, composite, "jar");
				addJarVariants(imageRegistry, composite, "jarCopyFromSoftwareFm");
				addJarVariants(imageRegistry, composite, "jarCopyToSoftwareFm");
				addJarVariants(imageRegistry, composite, "jarBreakLink");

				crossProductArtifactsAndOverlays(imageRegistry, composite);

				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);

				return composite;
			}

			private void addJarVariants(ImageRegistry imageRegistry, Composite composite, String key) {
				Composite rowComposite = new Composite(composite, SWT.NULL);
				rowComposite.setLayout(Swts.getHorizonalNoMarginRowLayout());
				addTitle(rowComposite, key);
				boolean[] falseTrue = new boolean[] { false, true };
				for (boolean source : falseTrue)
					for (boolean javadoc : falseTrue)
						for (boolean sourceFm : falseTrue)
							for (boolean javadocFm : falseTrue) {
								Map<SmallIconPosition, String> map = Maps.newMap();
								if (javadoc)
									map.put(SmallIconPosition.TopLeft, "javadoc");
								if (source)
									map.put(SmallIconPosition.BottomLeft, "source");
								if (javadocFm)
									map.put(SmallIconPosition.TopRight, "softwareFm");
								if (sourceFm)
									map.put(SmallIconPosition.BottomRight, "softwareFm");
								makeJarVarient(rowComposite, imageRegistry, key, map);
							}
			}

			private void addTitle(Composite rowComposite, String string) {
				Label label = new Label(rowComposite, SWT.NULL);
				label.setText(string);
				label.setLayoutData(new RowData(150, SWT.DEFAULT));
			}

			private void makeJarVarient(Composite rowComposite, ImageRegistry imageRegistry, String key, Map<SmallIconPosition, String> map) {
				ImageButton button = new ImageButton(rowComposite, imageRegistry, "artifact." + key, false);
				for (Entry<SmallIconPosition, String> entry : map.entrySet()) {
					button.setSmallIcon(entry.getKey(), "smallIcon." + entry.getValue());
				}
			}

			private void crossProductArtifactsAndOverlays(ImageRegistry imageRegistry, Composite composite) {
				for (String name : getNamesFor(ArtifactsAnchor.class, "jar.png")) {
					Composite rowComposite = new Composite(composite, SWT.NULL);
					rowComposite.setLayout(Swts.getHorizonalNoMarginRowLayout());
					addTitle(rowComposite, name);
					String artifactKey = "artifact." + name;
					new ImageButton(rowComposite, imageRegistry, artifactKey, false);
					for (String overlay : getNamesFor(OverlaysAnchor.class, "add.png"))
						new ImageButton(rowComposite, imageRegistry, artifactKey, "overlay." + overlay, false);
				}
			}

			private void registerImages(Composite from, ImageRegistry imageRegistry, Class<?> anchor, String aName, String prefix) {
				for (String name : getNamesFor(anchor, aName + ".png"))
					imageRegistry.put(prefix + "." + name, Images.makeImage(from.getDisplay(), anchor, name + ".png"));
			}
		});
	}

	protected static Iterable<String> getNamesFor(Class<?> anchor, String anImageName) {
		try {
			File aFile = new ClassPathResource(anImageName, anchor).getFile();
			File directory = aFile.getParentFile();
			return Iterables.map(Iterables.iterable(directory.list(Files.extensionFilter("png"))), Files.noExtension());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}
