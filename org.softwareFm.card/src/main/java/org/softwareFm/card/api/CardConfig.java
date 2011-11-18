package org.softwareFm.card.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.internal.CardCollectionsDataStore;
import org.softwareFm.card.internal.details.DetailFactory;
import org.softwareFm.card.internal.details.IDetailAdder;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class CardConfig {

	public static final IFunction1<String, String> defaultCardTitleFn = Strings.lastSegmentFn("/");
	public final boolean debugModifiers = false;
	public final int cardNameWeight = 8;
	public final int cardValueWeight = 8;
	public final float cardMaxNameSizeRatio = 1.2f;
	public final int leftMargin;
	public final int rightMargin;
	public final int topMargin;
	public final int bottomMargin;
	public final int titleHeight;
	public final int titleIndentX = 10;
	public final int titleIndentY = 10;
	public final CardCollectionsDataStore cardCollectionsDataStore = new CardCollectionsDataStore();
	public final IFunction1<String, IResourceGetter> resourceGetterFn;
	public final ICardConfigSelector selector;
	public final IDetailFactory detailFactory;
	public final ICardFactory cardFactory;
	public final ICardDataStore cardDataStore;
	public final int cardStyle;
	public final boolean allowSelection;
	public final IFunction1<Map<String, Object>, Image> cardIconFn;
	public final IFunction1<String, String> cardTitleFn;
	public final IFunction1<String, Image> imageFn;
	public final IFunction1<LineItem, Image> iconFn;
	public final IFunction1<LineItem, String> nameFn;
	public final IFunction1<LineItem, String> valueFn;
	public final IFunction1<LineItem, Boolean> hideFn;
	public final IFunction1<ICard, String> defaultChildFn;
	public final Comparator<String> comparator;
	public final IFunction1<Map<String, Object>, Image> navIconFn;
	public final List<ICardDataModifier> keyValueModifiers;
	public final IFunction1<ICard, TitleSpec> titleSpecFn;
	public final IFollowOnFragment followOnFragment;
	public final int defaultWidthWeight = 3;
	public final int defaultHeightWeight = 2;

	public final IUrlGeneratorMap urlGeneratorMap;

	public int titleSpacer = 3;
	public int defaultMargin = 10;

	public int compressedNavTitleWidth = 12;
	public int navIconWidth = 10;
	public int cornerRadius = 7;
	public int cornerRadiusComp = 2; // if its one loose pixels at the corner
	public final IRightClickCategoriser rightClickCategoriser;

	public CardConfig(ICardFactory cardFactory, ICardDataStore cardDataStore) {
		this.resourceGetterFn = Functions.constant(IResourceGetter.Utils.noResources().with(//
				new ResourceGetterMock("card.name.title", "Name", //
						"card.value.title", "Value", //
						DisplayConstants.buttonOkTitle, "Ok",//
						DisplayConstants.buttonCancelTitle, "Cancel",//
						"navBar.prev.title", "<", //
						"navBar.next.title", ">",//
						"card.holder.loading.text", "loading")));
		this.selector = ICardConfigSelector.Utils.defaultSelector(this);
		this.detailFactory = new DetailFactory(Collections.<IDetailAdder> emptyList());
		this.cardFactory = cardFactory;
		this.cardDataStore = cardDataStore;
		this.cardStyle = SWT.FULL_SELECTION | SWT.NO_SCROLL;
		this.allowSelection = true;
		this.cardIconFn = Functions.constant(null);
		this.navIconFn = Functions.constant(null);
		this.cardTitleFn = defaultCardTitleFn;
		this.imageFn = Functions.constant(null);
		this.iconFn = Functions.constant(null);
		this.nameFn = LineItem.Utils.keyFn();
		this.valueFn = LineItem.Utils.valueAsStrFn();
		this.hideFn = Functions.constant(false);
		this.defaultChildFn = Functions.constant(null);
		this.titleSpecFn = Functions.expectionIfCalled();
		this.comparator = Lists.orderedComparator();
		this.leftMargin = defaultMargin;
		this.rightMargin = defaultMargin;
		this.topMargin = defaultMargin;
		this.bottomMargin = defaultMargin;
		this.titleHeight = 20;
		this.keyValueModifiers = Collections.emptyList();
		this.rightClickCategoriser = IRightClickCategoriser.Utils.noRightClickCategoriser();
		this.urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap();
		this.followOnFragment = new IFollowOnFragment() {
			@Override
			public String findFollowOnFragment(String key, Object value) {
				return null;
			}
		};
	}

	private CardConfig(IFunction1<String, IResourceGetter> resourceGetterFn, ICardConfigSelector selector, IDetailFactory detailFactory, ICardFactory cardFactory, ICardDataStore cardDataStore, int style, boolean allowSelection, IFunction1<Map<String, Object>, Image> cardIconFn, IFunction1<String, String> cardTitleFn, IFunction1<String, Image> imageFn, IFunction1<LineItem, Image> iconFn, IFunction1<LineItem, String> nameFn, IFunction1<LineItem, String> valueFn, IFunction1<ICard, String> defaultChildFn, IFunction1<LineItem, Boolean> hideFn, Comparator<String> comparator, int leftMargin, int rightMargin, int topMargin, int bottomMargin, int navBarHeight, IFunction1<Map<String, Object>, Image> navIconFn, List<ICardDataModifier> keyValueModifiers, IFollowOnFragment followOnFragment, IFunction1<ICard, TitleSpec> titleSpecFn, IRightClickCategoriser rightClickCategoriser, IUrlGeneratorMap urlGeneratorMap) {
		this.resourceGetterFn = resourceGetterFn;
		this.selector = selector;
		this.detailFactory = detailFactory;
		this.cardFactory = cardFactory;
		this.cardDataStore = cardDataStore;
		this.cardStyle = style;
		this.allowSelection = allowSelection;
		this.cardIconFn = cardIconFn;
		this.cardTitleFn = cardTitleFn;
		this.imageFn = imageFn;
		this.iconFn = iconFn;
		this.nameFn = nameFn;
		this.valueFn = valueFn;
		this.defaultChildFn = defaultChildFn;
		this.hideFn = hideFn;
		this.comparator = comparator;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
		this.titleHeight = navBarHeight;
		this.navIconFn = navIconFn;
		this.keyValueModifiers = keyValueModifiers;
		this.followOnFragment = followOnFragment;
		this.titleSpecFn = titleSpecFn;
		this.rightClickCategoriser = rightClickCategoriser;
		this.urlGeneratorMap = urlGeneratorMap;
	}

	public CardConfig withTitleFn(IFunction1<String, String> cardTitleFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withStyleAndSelection(int style, boolean allowSelection) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withNameFn(IFunction1<LineItem, String> nameFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withHideFn(IFunction1<LineItem, Boolean> hideFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withValueFn(IFunction1<LineItem, String> valueFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withCardIconFn(IFunction1<Map<String, Object>, Image> cardIconFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withResourceGetterFn(IFunction1<String, IResourceGetter> resourceGetterFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withAggregatorTags(List<String> tagNames) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withSorter(Comparator<String> comparator) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withMargins(int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withTitleHeight(int titleHeight) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withNavIconFn(IFunction1<Map<String, Object>, Image> navIconFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withKeyValueModifiers(List<ICardDataModifier> keyValueModifiers) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withDetailsFactory(IDetailFactory detailFactory) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withDefaultChildFn(IFunction1<ICard, String> defaultChildFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withFollowOn(IFollowOnFragment followOnFragment) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withTitleSpecFn(IFunction1<ICard, TitleSpec> titleSpecFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withCardFactory(ICardFactory cardFactory) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withRightClickCategoriser(IRightClickCategoriser rightClickCategoriser) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withImageFn(IFunction1<String, Image> imageFn) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public CardConfig withUrlGeneratorMap(IUrlGeneratorMap urlGeneratorMap) {
		return new CardConfig(resourceGetterFn, selector, detailFactory, cardFactory, cardDataStore, cardStyle, allowSelection, cardIconFn, cardTitleFn, imageFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn, rightClickCategoriser, urlGeneratorMap);
	}

	public Map<String, Object> modify(String url, Map<String, Object> rawData) {
		Map<String, Object> value = rawData;
		debugModifiers("Initial " + url, value);
		for (ICardDataModifier modifier : keyValueModifiers) {
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

}
