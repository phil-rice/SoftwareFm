package org.softwareFm.swt;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.images.BasicImageRegisterConfigurator;
import org.softwareFm.images.artifacts.ArtifactsAnchor;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.swt.Swts.Show;
import org.softwareFm.swt.swt.Swts.Size;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

public class TitleWithPainListenerMain {
	public static void main(String[] args) {
		Show.displayNoLayout(TitleWithTitlePaintListener.class.getSimpleName(), new IFunction1<Composite, Composite>() {
	
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite parent = new Composite(from, SWT.NULL);
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				Color color = new Color(from.getDisplay(), 183, 196, 183);
				TitleSpec titleSpec = new TitleSpec(imageRegistry.get(ArtifactsAnchor.artifactKey), color, color, 20);
				TitleWithTitlePaintListener titleWithTitlePaintListener = new TitleWithTitlePaintListener(parent, cardConfig, titleSpec, "title", "tooltip");
				Size.resizeMeToParentsSize(parent);
				Size.resizeMeToParentsSize(titleWithTitlePaintListener.canvas);
				return parent;
			}
		});
	}
}

