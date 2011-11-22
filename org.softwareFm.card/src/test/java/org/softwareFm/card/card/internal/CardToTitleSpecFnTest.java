package org.softwareFm.card.card.internal;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.card.CardMock;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.ICardConfigurator;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

//Smoke test. 
public class CardToTitleSpecFnTest extends SwtIntegrationTest{

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
