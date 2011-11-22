package org.softwareFm.card.title;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;

public class Title implements IHasControl {

	final Canvas canvas;
	private final TitlePaintListener listener;

	public Title(Composite parent, final CardConfig cardConfig, final TitleSpec initialTitleSpec, String initialTitle, String initialTooltip) {
		canvas = new Canvas(parent, SWT.NONE) {
			@Override
			public Rectangle getClientArea() {
				Rectangle ca = super.getClientArea();
				return new Rectangle(ca.x + cardConfig.leftMargin, ca.y + cardConfig.topMargin, ca.width - cardConfig.leftMargin - cardConfig.rightMargin, ca.height - cardConfig.topMargin);
			}
		};
		canvas.setToolTipText(initialTooltip);
		listener = new TitlePaintListener(cardConfig, initialTitleSpec, initialTitle);
		canvas.addPaintListener(listener);
	}

	public void setTitleAndImage(String title, String tooltip, TitleSpec titleSpec) {
		canvas.setToolTipText(tooltip);
		listener.setTitleAndTitleSpec(title, titleSpec);
		canvas.redraw();
	}

	public String getText() {
		return listener.getTitle();
	}

	@Override
	public Control getControl() {
		return canvas;
	}

	public static void main(String[] args) {
		Show.displayNoLayout(Title.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(Composite from) throws Exception {
				Composite parent = new Composite(from, SWT.NULL);
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				TitleSpec titleSpec = new TitleSpec(imageRegistry.get(ArtifactsAnchor.artifactKey), new Color(from.getDisplay(), 183, 196, 183), 20);
				Title title = new Title(parent, cardConfig, titleSpec, "title", "tooltip");
				Size.resizeMeToParentsSize(parent);
				Size.resizeMeToParentsSize(title.canvas);
				return parent;
			}
		});
	}

}
