package org.softwareFm.display.smallButtons;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.SoftwareFmLayout;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class ImagesInALine {
	public static void main(String[] args) {
		Show.display("Images in a line", new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				CompositeConfig config = new CompositeConfig(from.getDisplay(), new SoftwareFmLayout(), imageRegistry, IResourceGetter.Utils.noResources());
				TitleAndText titleAndText = new TitleAndText(config, composite, "Title", false);
				new SimpleImageButton(titleAndText, config.imageButtonConfig.withImage(ArtifactsAnchor.artifactKey), true);
				new SimpleImageButton(titleAndText, config.imageButtonConfig.withImage(ArtifactsAnchor.artifactKey, OverlaysAnchor.editKey), true);
				new SimpleImageButton(titleAndText, config.imageButtonConfig.withImage(GeneralAnchor.browseKey), true);
				Grid.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}

		});
	}
}
