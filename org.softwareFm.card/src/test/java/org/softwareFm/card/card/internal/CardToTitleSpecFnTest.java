/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.card.CardMock;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

//Smoke test. 
public class CardToTitleSpecFnTest extends SwtTest{

	private CardToTitleSpecFn cardToTitleSpecFn;
	private CardConfig cardConfig;
	private IFunction1<String, IResourceGetter> resourceGetterFn;
	private IFunction1<String, Image> imageFn;

	public void test() throws Exception {
		checkTitleSpec("someUrl/groupUrl", "group", 243, 232, 252, ArtifactsAnchor.groupKey, 20);
	}

	private void checkTitleSpec(String url, String cardType, int red, int green, int blue, String imageKey, int indent) throws Exception {
		TitleSpec titleSpec = cardToTitleSpecFn.apply(new CardMock(null, cardConfig, url, Maps.stringObjectLinkedMap(CardConstants.slingResourceType, cardType)));
		assertEquals(red,titleSpec.background.getRed());
		assertEquals(green,titleSpec.background.getGreen());
		assertEquals(blue,titleSpec.background.getBlue());
		assertEquals(imageFn.apply(imageKey), titleSpec.icon);
		assertEquals(indent, titleSpec.rightIndent);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		imageFn = ICardConfigurator.Utils.imageFn(shell.getDisplay(), new BasicImageRegisterConfigurator());
		resourceGetterFn = ICardConfigurator.Utils.resourceGetterFn(CardConfig.class, "Card");
		cardConfig = CardDataStoreFixture.syncCardConfig(shell.getDisplay()).withImageFn(imageFn).withResourceGetterFn(resourceGetterFn);
		cardToTitleSpecFn = new CardToTitleSpecFn(shell.getDisplay(), imageFn);
	}
	
}