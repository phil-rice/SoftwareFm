/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
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