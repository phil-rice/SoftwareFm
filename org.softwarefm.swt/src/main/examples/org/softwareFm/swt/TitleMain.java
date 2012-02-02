package org.softwareFm.swt;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.images.BasicImageRegisterConfigurator;
import org.softwareFm.images.artifacts.ArtifactsAnchor;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.swt.Swts.Show;
import org.softwareFm.swt.swt.Swts.Size;
import org.softwareFm.swt.title.Title;
import org.softwareFm.swt.title.TitleSpec;

public class TitleMain {
	public static void main(String[] args) {
		Show.displayNoLayout(Title.class.getSimpleName(), new IFunction1<Composite, Composite>() {
	
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite parent = new Composite(from, SWT.NULL);
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				Color color = new Color(from.getDisplay(), 183, 196, 183);
				TitleSpec titleSpec = new TitleSpec(imageRegistry.get(ArtifactsAnchor.artifactKey), color, color, 20);
				Title titleWithTitlePaintListener = new Title(parent, cardConfig, titleSpec, "title", "tooltip");
				Size.resizeMeToParentsSize(parent);
				Size.resizeMeToParentsSize(titleWithTitlePaintListener.canvas);
				return parent;
			}
		});
	}
	
}

