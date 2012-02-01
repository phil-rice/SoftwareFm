/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.title;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.swt.configuration.CardConfig;

public class Title implements IHasControl {

	final Canvas canvas;
	private TitleSpec titleSpec;
	private String title;

	public Title(Composite parent, final CardConfig cardConfig, final TitleSpec initialTitleSpec, String initialTitle, String initialTooltip) {
		this.titleSpec = initialTitleSpec;
		this.title = initialTitle;
		canvas = new Canvas(parent, SWT.NONE);
		canvas.setToolTipText(initialTooltip);
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				Rectangle b = canvas.getClientArea();
				e.gc.setBackground(titleSpec.titleColor);

				e.gc.fillRoundRectangle(b.x, b.y, b.width, b.height * 2, cardConfig.cornerRadius, cardConfig.cornerRadius);

				if (titleSpec.icon != null) {
					int iconX = b.x + cardConfig.titleSpacer;
					e.gc.drawImage(titleSpec.icon, iconX, b.y + 1);
				}
				int leftX = titleSpec.icon == null ? b.x + cardConfig.titleSpacer : b.x + 2 * cardConfig.titleSpacer + titleSpec.icon.getImageData().width;
				e.gc.setClipping(b.x, b.y, b.width - titleSpec.rightIndent - cardConfig.cornerRadius, b.height);
				e.gc.drawText(title, leftX, b.y + 3);
			}
		});
	}

	public void setTitleAndImage(String title, String tooltip, TitleSpec titleSpec) {
		if (title == null)
			throw new NullPointerException();
		this.title = title;
		this.titleSpec = titleSpec;
		canvas.setToolTipText(tooltip);
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