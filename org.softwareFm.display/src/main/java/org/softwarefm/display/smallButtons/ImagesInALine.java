package org.softwarefm.display.smallButtons;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.backdrop.BackdropAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwarefm.display.SoftwareFmLayout;

public class ImagesInALine {
	public static void main(String[] args) {
		Swts.display("Images in a line", new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				Composite parent = new Composite(composite, SWT.NULL);
				parent.setLayout(Swts.getHorizonalNoMarginRowLayout());
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				ImageButtonConfig config = new ImageButtonConfig(new SoftwareFmLayout(), imageRegistry, BackdropAnchor.depressed, BackdropAnchor.main, null, null);
				new SimpleImageButton(parent, config.withImage(ArtifactsAnchor.projectKey)).getControl().setLayoutData(new RowData(18, 18));
				new SimpleImageButton(parent, config.withImage(ArtifactsAnchor.projectKey, OverlaysAnchor.editKey)).getControl().setLayoutData(new RowData(18, 18));
				new SimpleImageButton(parent, config.withImage(GeneralAnchor.browseKey)).getControl().setLayoutData(new RowData(18, 18));
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}
		});
	}
}
