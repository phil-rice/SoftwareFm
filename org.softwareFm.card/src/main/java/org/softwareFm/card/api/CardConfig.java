package org.softwareFm.card.api;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.softwareFm.card.internal.DetailFactory;
import org.softwareFm.card.internal.KeyValueAggregator;
import org.softwareFm.display.data.ResourceGetterMock;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class CardConfig {

	public static final IFunction1<String, String> defaultCardTitleFn = Strings.lastSegmentFn("/");

	public final int leftMargin;
	public final int rightMargin;
	public final int topMargin;
	public final int bottomMargin;
	public final int titleHeight;

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
	public final Comparator<KeyValue> comparator;
	public final IFunction1<Map<String, Object>, List<KeyValue>> aggregator;

	public final int defaultWidthWeight = 3;
	public final int defaultHeightWeight = 2;

	public CardConfig(ICardFactory cardFactory, ICardDataStore cardDataStore) {
		this.resourceGetter = IResourceGetter.Utils.noResources().with(new ResourceGetterMock("card.name.title", "Name", "card.value.title", "Value", "navBar.prev.title", "<", "navBar.next.title", ">"));
		this.selector = ICardConfigSelector.Utils.defaultSelector(this);
		this.detailFactory = new DetailFactory();
		this.cardFactory = cardFactory;
		this.cardDataStore = cardDataStore;
		this.style = SWT.FULL_SELECTION | SWT.NO_SCROLL;
		this.allowSelection = false;
		this.cardIconFn = Functions.constant(null);
		this.cardTitleFn = defaultCardTitleFn;
		this.iconFn = Functions.constant(null);
		this.nameFn = KeyValue.Utils.keyFn();
		this.valueFn = KeyValue.Utils.valueAsStrFn();
		this.hideFn = Functions.constant(false);
		this.comparator = KeyValue.Utils.orderedKeyComparator();
		this.aggregator = new KeyValueAggregator(null);
		this.leftMargin = 5;
		this.rightMargin = 5;
		this.topMargin = 5;
		this.bottomMargin = 5;
		this.titleHeight = 20;

	}

	private CardConfig(IResourceGetter resourceGetter, ICardConfigSelector selector, IDetailFactory detailFactory, ICardFactory cardFactory, ICardDataStore cardDataStore, int style, boolean allowSelection, IFunction1<Map<String, Object>, Image> cardIconFn, IFunction1<String, String> cardTitleFn, IFunction1<KeyValue, Image> iconFn, IFunction1<KeyValue, String> nameFn, IFunction1<KeyValue, String> valueFn, IFunction1<KeyValue, Boolean> hideFn, Comparator<KeyValue> comparator, IFunction1<Map<String, Object>, List<KeyValue>> aggregator, int leftMargin, int rightMargin, int topMargin, int bottomMargin, int navBarHeight) {
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
		this.hideFn = hideFn;
		this.comparator = comparator;
		this.aggregator = aggregator;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
		this.titleHeight = navBarHeight;
	}

	public CardConfig withTitleFn(IFunction1<String, String> cardTitleFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);

	}

	public CardConfig withStyleAndSelection(int style, boolean allowSelection) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

	public CardConfig withNameFn(IFunction1<KeyValue, String> nameFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

	public CardConfig withHideFn(IFunction1<KeyValue, Boolean> hideFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

	public CardConfig withValueFn(IFunction1<KeyValue, String> valueFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

	public CardConfig withCardIconFn(IFunction1<Map<String, Object>, Image> cardIconFn) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

	public CardConfig withResourceGetter(IResourceGetter resourceGetter) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

	public CardConfig withAggregatorTag(String tag) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, new KeyValueAggregator(tag), leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

	public CardConfig withSorter(Comparator<KeyValue> comparator) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

	public CardConfig withMargins(int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}
	public CardConfig withTitleHeight(int titleHeight) {
		return new CardConfig(resourceGetter, selector, detailFactory, cardFactory, cardDataStore, style, allowSelection, cardIconFn, cardTitleFn, iconFn, nameFn, valueFn, hideFn, comparator, aggregator, leftMargin, rightMargin, topMargin, bottomMargin, titleHeight);
	}

}
