/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.IRightClickCategoriser;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.ICardAndCollectionsDataStore;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.dataStore.IFollowOnFragment;
import org.softwareFm.card.dataStore.internal.CardCollectionsDataStore;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailFactory;
import org.softwareFm.card.details.internal.DetailFactory;
import org.softwareFm.card.details.internal.EditorDetailAdder;
import org.softwareFm.card.editors.IEditorDetailAdder;
import org.softwareFm.card.modifiers.ICardDataModifier;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.menu.IPopupMenuService;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.resources.ResourceGetterMock;
import org.softwareFm.utilities.strings.Strings;

/** This holds all the layout data, and strategies associated with displaying a card. It is meant to be immutable, although the image functions often rely on ImageRegisters, and are thus subject to mutability. */
public class CardConfig {

	/** If you don't specify a {@link #cardTitleFn} this is the default */
	public static final IFunction1<ICard, String> defaultCardTitleFn = new IFunction1<ICard, String>() {
		@Override
		public String apply(ICard from) throws Exception {
			return Strings.lastSegment(from.url(), "/");
		}
	};
	/** Enabled the debugging of chain of modifiers {@link #cardDataModifiers} that change the raw data from the server into display data */
	public final boolean debugModifiers = false;
	/** The {@link org.softwareFm.card.card.internal.Card.CardLayout} uses this to determine how wide to display the name column. The bigger the more space it takes up */
	public final int cardNameWeight = 8;
	/** The {@link org.softwareFm.card.card.internal.Card.CardLayout} uses this to determine how wide to display the value column. The bigger the more space it takes up */
	public final int cardValueWeight = 8;
	/** The {@link org.softwareFm.card.card.internal.Card.CardLayout} uses this to determine how wide to display the name column. Once the name column is 'this' much bigger than the ideal name width, it stops getting bigger */
	public final float cardMaxNameSizeRatio = 1.2f;
	/** All the margins are set to this by default */
	public int defaultMargin = 10;
	public final int leftMargin;
	public final int rightMargin;
	public final int topMargin;
	public final int bottomMargin;
	public final int cardIconWidth = 16;
	public final int cardIconHeight = 16;;

	/** The title for the card is this big */
	public final int titleHeight;
	/** The editors indent the edit control by this much */
	public final int editorIndentX = 10;
	/** The editors indent the edit control by this much */
	public final int editorIndentY = 10;
	/** The heightWeight and widthWeight determine the aspect ration of the card */
	public final int widthWeight = 3;
	/** The heightWeight and widthWeight determine the aspect ration of the card */
	public final int heightWeight = 2;
	/** The spacer between sections in a title */
	public int titleSpacer = 3;

	/** When an item in the navigation bar needs to be compressed, because the whole 'url' is too big to be displayed, this is the width it is compressed to */
	public int compressedNavTitleWidth = 12;
	/** The width of the diagonal icons in the nav bar */
	public int navIconWidth = 10;
	/** The cornerradius of the lines surrounding the card */
	public int cornerRadius = 7;
	/** Some components are pulled back from the edge by this amount, to avoid drawing over the line */
	public int cornerRadiusComp = 2; // if its one loose pixels at the corner

	/** The softwarefm application uses this to map group / artifact / version / digest data to urls on the server. It can be used to map any identifiers to data on the server */
	public final IUrlGeneratorMap urlGeneratorMap;
	/** This gets data from the server. */
	public final ICardDataStore cardDataStore;
	/** When data is asked about a card, often more data is asked: specifically any collections are asked for. This manages that 'extra data' getting */
	public final ICardAndCollectionsDataStore cardCollectionsDataStore = new CardCollectionsDataStore();
	/** this is the function used by the {@link #cardCollectionsDataStore} to determine what extra data needs to be acquired. Typically used to request more data about children */
	public final IFollowOnFragment followOnFragment;

	/** * This is a function from 'cardType' (typically found by ICard.cardtype(), or by finding the sling:resourceType in a map) to a resourceGetter. The implementation used by the main softwarefm application uses a root properties file, and then each cardtype has <cardtype>.properties. Thus each cardtype can have it's own order / naming patterns / etc */
	public final IFunction1<String, IResourceGetter> resourceGetterFn;

	/** This makes the cards */
	public final ICardFactory cardFactory;

	/** the int 'style' field used by the Card. */
	public final int cardStyle;
	/** Can you select the lines on the card? */
	public final boolean allowSelection;
	/** When the user clicks on a line in the card, the detail factory makes the component that is displayed in the detail area */
	public final IDetailFactory detailFactory;
	/** the {@link EditorDetailAdder} uses this to create the editor. The string is the name specified in the properties file. For example in card.properties we set editor.decription=styledText. When clicking on description, the EditorDetailAdder will pass 'styledText' to this function. If it returns null no editor will be shown */
	public final IFunction1<String, IEditorDetailAdder> editorFn;
	/** Derive the title of the card from the card */
	public final IFunction1<ICard, String> cardTitleFn;
	/** Given a string, find an image. Typically backed by an ImageRegister */
	public final IFunction1<String, Image> imageFn;
	/** Given a line on a card, find an image for that line */
	public final IFunction1<LineItem, Image> iconFn;
	/** Given a line on a card, find the display name for that line */
	public final ILineItemFunction<String> nameFn;
	/** Given a line on a card, find the value for that line */
	public final ILineItemFunction<String> valueFn;
	/** Given a line on a card, should it be displayed? */
	public final ILineItemFunction<Boolean> hideFn;

	/** Given the data in a card, which is the item that should be (by default) shown in the details area. */
	public final IFunction1<ICard, String> defaultChildFn;

	/** The {@link TitleSpec} holds data about the title: image, color, and the right indent */
	public final IFunction1<ICardData, TitleSpec> titleSpecFn;
	/** This is used when displaying the navigation bar to determine the image to be shown instead of a '/' between url segments */
	public final IFunction1<Map<String, Object>, Image> navIconFn;
	/** A list of modifiers that adjust the data shown on the card. Typically this will aggregate / sort / enrich... etc */
	public final List<ICardDataModifier> cardDataModifiers;

	/** Used by the right click menu on the card to work out 'what' has been clicked on. Mostly it is concerned on 'what sort of collection is it' */
	public final IRightClickCategoriser rightClickCategoriser;

	public final IPopupMenuService<ICard> popupMenuService ;
	public final String popupMenuId;
	public final String detailsPopupMenuId;

	public CardConfig(ICardFactory cardFactory, ICardDataStore cardDataStore) {
		this.resourceGetterFn = Functions.constant(IResourceGetter.Utils.noResources().with(//
				new ResourceGetterMock("card.name.title", "Name", //
						"card.value.title", "Value", //
						DisplayConstants.buttonOkTitle, "Ok",//
						CardConstants.buttonTestTitle, "Test",//
						DisplayConstants.buttonCancelTitle, "Cancel",//
						CardConstants.cardNameUrlKey, "{0}",//
						CardConstants.cardHolderLoadingText, "loading")));
		this.detailFactory = new DetailFactory(Collections.<IDetailAdder> emptyList());
		this.cardFactory = cardFactory;
		this.cardDataStore = cardDataStore;
		this.cardStyle = SWT.FULL_SELECTION | SWT.NO_SCROLL;
		this.allowSelection = true;
		this.navIconFn = Functions.constant(null);
		this.cardTitleFn = defaultCardTitleFn;
		this.imageFn = Functions.constant(null);
		this.iconFn = Functions.constant(null);
		this.nameFn = LineItem.Utils.keyLineItemFn();
		this.valueFn = LineItem.Utils.valueAsStrLineItemFn();
		this.hideFn = ILineItemFunction.Utils.falseFn();
		this.defaultChildFn = Functions.constant(null);
		this.titleSpecFn = Functions.expectionIfCalled();
		this.leftMargin = defaultMargin;
		this.rightMargin = defaultMargin;
		this.topMargin = defaultMargin;
		this.bottomMargin = defaultMargin;
		this.titleHeight = 20;
		this.cardDataModifiers = Collections.emptyList();
		this.rightClickCategoriser = IRightClickCategoriser.Utils.noRightClickCategoriser();
		this.urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap();
		this.followOnFragment = new IFollowOnFragment() {
			@Override
			public String findFollowOnFragment(String key, Object value) {
				return null;
			}
		};
		this.editorFn = Functions.constant(null);
		this.popupMenuId = null;
		this.detailsPopupMenuId = null;
		this.popupMenuService=IPopupMenuService.Utils.popUpMenuService();
	}

	private CardConfig(IFunction1<String, IResourceGetter> resourceGetterFn, IDetailFactory detailFactory, ICardFactory cardFactory, ICardDataStore cardDataStore, int style, boolean allowSelection, IFunction1<ICard, String> cardTitleFn, IFunction1<String, Image> imageFn, IFunction1<LineItem, Image> iconFn, ILineItemFunction<String> nameFn, ILineItemFunction<String> valueFn, IFunction1<ICard, String> defaultChildFn, ILineItemFunction<Boolean> hideFn, int leftMargin, int rightMargin, int topMargin, int bottomMargin, int navBarHeight, IFunction1<Map<String, Object>, Image> navIconFn, List<ICardDataModifier> keyValueModifiers, IFollowOnFragment followOnFragment, IFunction1<ICardData, TitleSpec> titleSpecFn, IRightClickCategoriser rightClickCategoriser, IUrlGeneratorMap urlGeneratorMap, IFunction1<String, IEditorDetailAdder> editorFn, String popupMenuId, IPopupMenuService<ICard> popupMenuService, String detailsPopupMenuId) {
		this.resourceGetterFn = resourceGetterFn;
		this.detailFactory = detailFactory;
		this.cardFactory = cardFactory;
		this.cardDataStore = cardDataStore;
		this.cardStyle = style;
		this.allowSelection = allowSelection;
		this.cardTitleFn = cardTitleFn;
		this.imageFn = imageFn;
		this.iconFn = iconFn;
		this.nameFn = nameFn;
		this.valueFn = valueFn;
		this.defaultChildFn = defaultChildFn;
		this.hideFn = hideFn;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
		this.titleHeight = navBarHeight;
		this.navIconFn = navIconFn;
		this.cardDataModifiers = keyValueModifiers;
		this.followOnFragment = followOnFragment;
		this.titleSpecFn = titleSpecFn;
		this.rightClickCategoriser = rightClickCategoriser;
		this.urlGeneratorMap = urlGeneratorMap;
		this.editorFn = editorFn;
		this.popupMenuId = popupMenuId;
		this.popupMenuService = popupMenuService;
		this.detailsPopupMenuId = detailsPopupMenuId;
	}

	

	public CardConfig withPopupMenuId(String popupMenuId, String detailsPopupMenuId) {
		if (popupMenuId == this.popupMenuId&& this.detailsPopupMenuId == detailsPopupMenuId)
			return this;
		else
			return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);

	}

	public CardConfig withTitleFn(IFunction1<ICard, String> cardTitleFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withStyleAndSelection(int style, boolean allowSelection) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withNameFn(ILineItemFunction<String> nameFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withHideFn(ILineItemFunction<Boolean> hideFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withValueFn(ILineItemFunction<String> valueFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withResourceGetterFn(IFunction1<String, IResourceGetter> resourceGetterFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withMargins(int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withTitleHeight(int titleHeight) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withNavIconFn(IFunction1<Map<String, Object>, Image> navIconFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withIconFn(IFunction1<LineItem, Image> iconFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withKeyValueModifiers(ICardDataModifier... keyValueModifiers) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, Arrays.asList(keyValueModifiers), followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withDetailsFactory(IDetailFactory detailFactory) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withDefaultChildFn(IFunction1<ICard, String> defaultChildFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withFollowOn(IFollowOnFragment followOnFragment) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withTitleSpecFn(IFunction1<ICardData, TitleSpec> titleSpecFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withCardFactory(ICardFactory cardFactory) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withRightClickCategoriser(IRightClickCategoriser rightClickCategoriser) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withImageFn(IFunction1<String, Image> imageFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withUrlGeneratorMap(IUrlGeneratorMap urlGeneratorMap) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public CardConfig withEditorFn(IFunction1<String, IEditorDetailAdder> editorFn) {
		return new CardConfig(resourceGetterFn, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, cardDataModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap, editorFn, popupMenuId, popupMenuService, detailsPopupMenuId);
	}

	public Map<String, Object> modify(String url, Map<String, Object> rawData) {
		Map<String, Object> value = rawData;
		debugModifiers("Initial " + url, value);
		for (ICardDataModifier modifier : cardDataModifiers) {
			value = modifier.modify(this, url, value);
			debugModifiers(modifier.getClass().getSimpleName(), value);
		}
		return value;
	}

	private void debugModifiers(String message, Map<String, Object> value) {
		if (debugModifiers) {
			System.out.println(message);
			System.out.println(value);
			System.out.println();
		}
	}

	public static IResourceGetter resourceGetter(ICard card) {
		CardConfig cardConfig = card.getCardConfig();
		return Functions.call(cardConfig.resourceGetterFn, card.cardType());
	}

	public IFunction1<LineItem, String> valueFn() {
		return new IFunction1<LineItem, String>() {
			@Override
			public String apply(LineItem from) throws Exception {
				return valueFn.apply(CardConfig.this, from);
			}
		};
	}

	public IFunction1<LineItem, String> nameFn() {
		return new IFunction1<LineItem, String>() {
			@Override
			public String apply(LineItem from) throws Exception {
				return nameFn.apply(CardConfig.this, from);
			}
		};
	}

	public IFunction1<LineItem, Boolean> hideFn() {
		return new IFunction1<LineItem, Boolean>() {
			@Override
			public Boolean apply(LineItem from) throws Exception {
				return hideFn.apply(CardConfig.this, from);
			}
		};
	}

}