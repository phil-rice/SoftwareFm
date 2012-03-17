/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.IHasCard;
import org.softwareFm.swt.card.IHasKeyAndValue;
import org.softwareFm.swt.constants.CardConstants;

public class ExplorerNavigationIntegrationTest extends AbstractExplorerIntegrationTest {

	private final String orgFirstGroupUrl = "/org/first/org.first";

	public void testNavigatingTutorials() {
		postArtifactData();
		displayCard(artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(final ICardHolder cardHolder, ICard card) throws Exception {
				Map<String, Object> expectedValue = Maps.stringObjectMap(//
						CardConstants.slingResourceType, CardConstants.collection,//
						"one", Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"),//
						"two", Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"));
				clickOnItemAndCheckCollection(card, "Tutorials", "tutorial", expectedValue);
				ICard childOne = checkChildUrl(0, Urls.composeWithSlash(rootArtifactUrl, artifactUrl, "tutorial", "one"));
				checkChildUrl(1, Urls.composeWithSlash(rootArtifactUrl, artifactUrl, "tutorial", "two"));
				clickOnChildCardAndCheckUrl(cardHolder, childOne);
			}
		});
	}

	public void testNavigatingAboveRepository() {// issue-16
		displayCard("/org", new CardHolderAndCardCallback() {
			@Override
			public void process(final ICardHolder cardHolder, ICard card) throws Exception {
				clickOnItemAndCheckCollection(card, "First", "first", Maps.stringObjectMap("org.first", Maps.emptyStringObjectMap()));
				final String expectedUrl = Urls.composeWithSlash(rootArtifactUrl, orgFirstGroupUrl);
				ICard childCard = checkChildUrl(0, expectedUrl);
				clickOnChildCardAndCheckUrl(cardHolder, childCard);
			}

		});
	}

	private void clickOnItemAndCheckCollection(ICard card, String name, String expectedKey, Map<String, Object> expectedValue) {
		selectItemAndNotifyListeners(card, name);
		dispatchUntilQueueEmpty();
		ScrolledComposite detailContent = (ScrolledComposite) masterDetailSocial.getDetailContent();
		IHasKeyAndValue hasKeyAndValue = (IHasKeyAndValue) detailContent.getContent();
		assertEquals(expectedKey, hasKeyAndValue.getKey());
		assertEquals(expectedValue, hasKeyAndValue.getValue());
	}

	private void clickOnChildCardAndCheckUrl(final ICardHolder cardHolder, ICard childCard) {
		final String expectedUrl = childCard.url();
		childCard.getTable().notifyListeners(SWT.Selection, new Event());
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return expectedUrl.equals(cardHolder.getCard().url());
			}
		});
	}

	private ICard checkChildUrl(int index, final String expectedUrl) {
		ScrolledComposite detailContent = (ScrolledComposite) masterDetailSocial.getDetailContent();
		assertEquals(1, detailContent.getChildren().length);
		Control firstChild = detailContent.getChildren()[0];
		final Control firstGrandChild = ((Composite) firstChild).getChildren()[index];
		firstGrandChild.notifyListeners(SWT.Paint, new Event());
		// need to wait for data to be set up
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				ICard childCard = ((IHasCard) firstGrandChild).getCard();
				boolean result = childCard != null;
				return result;
			}
		});
		ICard childCard = ((IHasCard) firstGrandChild).getCard();
		assertEquals(expectedUrl, childCard.url());
		return childCard;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		File actualRoot = new File(remoteRoot, rootArtifactUrl);
		File file = new File(actualRoot, orgFirstGroupUrl);
		file.mkdirs();
	}

}