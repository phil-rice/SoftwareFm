package org.softwareFm.card.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.internal.CardCollectionsDataStore;
import org.softwareFm.card.internal.details.DetailFactory;
import org.softwareFm.card.internal.details.IDetailAdder;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.card.internal.modifiers.CardMapSorter;
import org.softwareFm.display.constants.DisplayConstants;
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
	public  final CardCollectionsDataStore cardCollectionsDataStore = new CardCollectionsDataStore();
	public final IResourceGetter resourceGetter;
	public final ICardConfigSelector selector;
	public final IDetailFactory detailFactory;
	public final ICardFactory cardFactory;
	public final ICardDataStore cardDataStore;
	public final int style;
	public final boolean allowSelection;
	public final IFunction1<Map<String, Object>, Image> cardIconFn;
	public final IFunction1<String, String> cardTitleFn;
	public final IFunction1<KeyValue, Image> iconFn;
	public final IFunction1<KeyValue, String> nameFn;
	public final IFunction1<KeyValue, String> valueFn;
	public final IFunction1<KeyValue, Boolean> hideFn;
	public final IFunction1<ICard, String> defaultChildFn;
	public final Comparator<String> comparator;
	public final IFunction1<Map<String, Object>, Image> navIconFn;
	public final List<ICardDataModifier> keyValueModifiers;
	public final IFunction1<ICard, TitleSpec> titleSpecFn ;
	public final IFollowOnFragment followOnFragment;
	public final int defaultWidthWeight = 3;
	public final int defaultHeightWeight = 2;

	public int titleSpacer = 3;

	public int compressedNavTitleWidth = 12;
	public int navIconWidth = 10;
	public int cornerRadius = 10;

	public CardConfig(ICardFactory cardFactory, ICardDataStore cardDataStore) {
		this.resourceGetter = IResourceGetter.Utils.noResources().with(//
				new ResourceGetterMock("card.name.title", "Name", //
						"card.value.title", "Value", //
						DisplayConstants.buttonOkTitle, "Ok",//
						DisplayConstants.buttonCancelTitle, "Cancel",//
						"navBar.prev.title", "<", //
						"navBar.next.title", ">",//
						"card.holder.loading.text", "loading"));
		this.selector = ICardConfigSelector.Utils.defaultSelector(this);
		this.detailFactory = new DetailFactory(Collections.<IDetailAdder> emptyList());
		this.cardFactory = cardFactory;
		this.cardDataStore = cardDataStore;
		this.style = SWT.FULL_SELECTION | SWT.NO_SCROLL;
		this.allowSelection = false;
		this.cardIconFn = Functions.constant(null);
		this.navIconFn = Functions.constant(null);
		this.cardTitleFn = defaultCardTitleFn;
		this.iconFn = Functions.constant(null);
		this.nameFn = KeyValue.Utils.keyFn();
		this.valueFn = KeyValue.Utils.valueAsStrFn();
		this.hideFn = Functions.constant(false);
		this.defaultChildFn = Functions.constant(null);
		this.titleSpecFn = Functions.expectionIfCalled();
		this.comparator = Lists.orderedComparator();
		this.leftMargin = 3;
		this.rightMargin = 3;
		this.topMargin = 3;
		this.bottomMargin = 3;
		this.titleHeight = 20;
		this.keyValueModifiers = Arrays.<ICardDataModifier> asList(new CardMapSorter());
		this.followOnFragment = new IFollowOnFragment() {
			@Override
			public String findFollowOnFragment(String key, Object value) {
				return null;
			}
		};
	}

	private CardConfig(IResourceGetter resourceGetter, ICardConfigSelector selector, IDetailFactory detailFactory, ICardFactory cardFactory, ICardDataStore cardDataStore, int style, boolean allowSelection, IFunction1<Map<String, Object>, Image> cardIconFn, IFunction1<String, String> cardTitleFn, IFunction1<KeyValue, Image> iconFn, IFunction1<KeyValue, String> nameFn, IFunction1<KeyValue, String> valueFn, IFunction1<ICard, String> defaultChildFn, IFunction1<KeyValue, Boolean> hideFn, Comparator<String> comparator, int leftMargin, int rightMargin, int topMargin, int bottomMargin, int navBarHeight, IFunction1<Map<String, Object>, Image> navIconFn, List<ICardDataModifier> keyValueModifiers, IFollowOnFragment followOnFragment, IFunction1<ICard, TitleSpec> titleSpecFn) {
		this.resourceGetter = resourceGetter;
		this.selector = selector;
		this.detailFactory = detailFactory;
		this.cardFactory = cardFactory;
		this.cardDataStore = cardDataStore;
		this.style = style;
		this.allowSelection = allowSelection;
		this.cardIconFn = cardIconFn;
		this.cardTitleFn = cardTitleFn;
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
	}

	public CardConfig withTitleFn(IFunction1<String, String> cardTitleFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withStyleAndSelection(int style, boolean allowSelection) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withNameFn(IFunction1<KeyValue, String> nameFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withHideFn(IFunction1<KeyValue, Boolean> hideFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withValueFn(IFunction1<KeyValue, String> valueFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withCardIconFn(IFunction1<Map<String, Object>, Image> cardIconFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withResourceGetter(IResourceGetter resourceGetter) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withAggregatorTags(List<String> tagNames) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withSorter(Comparator<String> comparator) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withMargins(int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withTitleHeight(int titleHeight) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withNavIconFn(IFunction1<Map<String, Object>, Image> navIconFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withKeyValueModifiers(List<ICardDataModifier> keyValueModifiers) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withDetailsFactory(IDetailFactory detailFactory) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withDefaultChildFn(IFunction1<ICard, String> defaultChildFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withFollowOn(IFollowOnFragment followOnFragment) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}
	
	public CardConfig withTitleSpecFn(IFunction1<ICard, TitleSpec> titleSpecFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public CardConfig withCardFactory(ICardFactory cardFactory) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, defaultChildFn, hideFn, comparator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight, navIconFn, keyValueModifiers, followOnFragment, titleSpecFn);
	}

	public Map<String, Object> modify(String url, Map<String, Object> rawData) {
		Map<String, Object> value = rawData;
		debugModifiers("Initial " +url, value);
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
