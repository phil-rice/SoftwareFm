package org.softwarefm.display.smallButtons;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.SoftwareFmLayout;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.composites.TitleAndText;

public class ImagesInALine {
	public static void main(String[] args) {
		Swts.display("Images in a line", new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				CompositeConfig config = new CompositeConfig(from.getDisplay(), new SoftwareFmLayout(), imageRegistry, IResourceGetter.Utils.noResources());
				TitleAndText titleAndText = new TitleAndText(config, composite, "Title", false);
				new SimpleImageButton(titleAndText, config.imageButtonConfig.withImage(ArtifactsAnchor.projectKey));
				new SimpleImageButton(titleAndText, config.imageButtonConfig.withImage(ArtifactsAnchor.projectKey, OverlaysAnchor.editKey));
				new SimpleImageButton(titleAndText, config.imageButtonConfig.withImage(GeneralAnchor.browseKey));
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}

		});
	}
}
