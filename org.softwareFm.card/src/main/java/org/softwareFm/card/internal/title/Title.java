package org.softwareFm.card.internal.title;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;

public class Title implements IHasControl {

	final Canvas canvas;
	private final TitlePaintListener listener;

	public Title(Composite parent, final CardConfig cardConfig, final TitleSpec initialTitleSpec, String initialTitle, String initialTooltip) {
		canvas = new Canvas(parent, SWT.NULL);
		canvas.setToolTipText(initialTooltip);
		listener = new TitlePaintListener(cardConfig, initialTitleSpec, initialTitle);
		canvas.addPaintListener(listener);
	}

	public void setTitleAndImage(String title, String tooltip, TitleSpec titleSpec) {
		canvas.setToolTipText(tooltip);
		listener.setTitleAndTitleSpec(title, titleSpec);
		canvas.redraw();
	}

	public String getText(){
		return listener.getTitle();
	}
	@Override
	public Control getControl() {
		return canvas;
	}

	public static void main(String[] args) {
		Swts.displayNoLayout(Title.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(Composite from) throws Exception {
				Composite parent = new Composite(from, SWT.NULL);
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				TitleSpec titleSpec = new TitleSpec(imageRegistry, ArtifactsAnchor.artifactKey, new Color(from.getDisplay(), 183, 196, 183), 20);
				Title title = new Title(parent, cardConfig, titleSpec, "title", "tooltip");
				Swts.resizeMeToParentsSize(parent);
				Swts.resizeMeToParentsSize(title.canvas);
				Swts.layoutDump(from);
				return parent;
			}
		});
	}

}
