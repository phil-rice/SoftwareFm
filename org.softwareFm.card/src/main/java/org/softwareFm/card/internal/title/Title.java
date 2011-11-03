package org.softwareFm.card.internal.title;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
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

	private final Canvas canvas;
	private String title;
	protected TitleSpec titleSpec;
	private String tooltip;

	public Title(Composite parent, final CardConfig cardConfig, final TitleSpec initialTitleSpec, String initialTitle, String initialTooltip) {
		this.titleSpec = initialTitleSpec;
		this.title = initialTitle;
		canvas = new Canvas(parent, SWT.NULL);
		canvas.setToolTipText(initialTooltip);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				Rectangle clientArea = canvas.getClientArea();
				e.gc.setBackground(titleSpec.background);
				e.gc.fillRoundRectangle(clientArea.x, clientArea.y, clientArea.width - titleSpec.rightIndent, clientArea.height + cardConfig.cornerRadius, cardConfig.cornerRadius, cardConfig.cornerRadius);

				if (titleSpec.icon != null)
					e.gc.drawImage(titleSpec.icon, clientArea.x+cardConfig.titleSpacer, clientArea.y+1);
				int leftX = titleSpec.icon == null ? clientArea.x + cardConfig.titleSpacer : clientArea.x + 2 * cardConfig.titleSpacer + titleSpec.icon.getImageData().width;
				e.gc.setClipping(clientArea.x, clientArea.y, clientArea.width - titleSpec.rightIndent-cardConfig.cornerRadius, clientArea.height);
				e.gc.drawText(title, leftX, clientArea.y);
				e.gc.setClipping((Rectangle) null);
				e.gc.drawRoundRectangle(clientArea.x, clientArea.y, clientArea.width - titleSpec.rightIndent, clientArea.height + cardConfig.cornerRadius, cardConfig.cornerRadius, cardConfig.cornerRadius);
			}

		});
	}

	public void setTitleAndImage(String title, String tooltip, TitleSpec titleSpec) {
		this.title = title;
		this.titleSpec = titleSpec;
		canvas.setToolTipText(tooltip);
		canvas.redraw();
	}

	public void setTitle(String title) {
		this.title = title;
		canvas.redraw();
	}

	@Override
	public Control getControl() {
		return canvas;
	}

	public String getText() {
		return title;
	}

	public static void main(String[] args) {
		Swts.displayNoLayout(Title.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(Composite from) throws Exception {
				Composite parent = new Composite(from, SWT.NULL);
				CardConfig cardConfig = CardDataStoreFixture.cardConfigSync;
				ImageRegistry imageRegistry = new ImageRegistry();
				new BasicImageRegisterConfigurator().registerWith(from.getDisplay(), imageRegistry);
				TitleSpec titleSpec = new TitleSpec(imageRegistry, ArtifactsAnchor.projectKey, new Color(from.getDisplay(), 183, 196, 183), 20);
				Title title = new Title(parent, cardConfig, titleSpec, "title", "tooltip");
				Swts.resizeMeToParentsSize(parent);
				Swts.resizeMeToParentsSize(title.canvas);
				Swts.layoutDump(from);
				return parent;
			}
		});
	}

}
