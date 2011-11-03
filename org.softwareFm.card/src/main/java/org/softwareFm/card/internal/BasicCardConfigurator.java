package org.softwareFm.card.internal;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardConfigurator;
import org.softwareFm.card.api.ICardDataModifier;
import org.softwareFm.card.api.IDetailFactory;
import org.softwareFm.card.api.IFollowOnFragment;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.internal.details.CollectionItemDetailAdder;
import org.softwareFm.card.internal.details.CollectionsDetailAdder;
import org.softwareFm.card.internal.details.ListDetailAdder;
import org.softwareFm.card.internal.details.TextEditorDetailAdder;
import org.softwareFm.card.internal.modifiers.CardMapSorter;
import org.softwareFm.card.internal.modifiers.CollectionsAggregatorModifier;
import org.softwareFm.card.internal.modifiers.FolderAggregatorModifier;
import org.softwareFm.card.internal.modifiers.KeyValueMissingItemsAdder;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.title.TitleAnchor;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class BasicCardConfigurator implements ICardConfigurator {

	@Override
	public CardConfig configure(Display display, CardConfig config) {
		final IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(CardConfig.class, "Card");
		String keysToHide = resourceGetter.getStringOrNull("keys.hide");
		final Set<String> ignoredNamed = keysToHide == null ? Collections.<String> emptySet() : new HashSet<String>(Arrays.asList(keysToHide.split(",")));
		IFunction1<KeyValue, String> nameFn = new IFunction1<KeyValue, String>() {
			@SuppressWarnings("unchecked")
			@Override
			public String apply(KeyValue from) throws Exception {
				if (from.value instanceof Map<?, ?>) {
					Map<Object, Object> map = (Map<Object, Object>) from.value;
					Object resourceType = map.get(CardConstants.slingResourceType);
					if (CardConstants.group.equals(resourceType))
						return from.key;
				}
				String key = findKey(from);
				String prettyKey = Strings.camelCaseToPretty(from.key);
				String pattern = resourceGetter.getStringOrNull(key + ".name");
				if (pattern == null)
					return prettyKey;
				else
					return MessageFormat.format(pattern, key, prettyKey);
			}
		};
		IFunction1<KeyValue, String> valueFn = new IFunction1<KeyValue, String>() {
			@Override
			public String apply(KeyValue from) throws Exception {
				String key = findKey(from);
				String pattern = resourceGetter.getStringOrNull(key + ".value");
				String size = findSize(from);
				if (pattern == null)
					if (from.value instanceof Map<?, ?>)
						return "";
					else
						return Strings.nullSafeToString(from.value);
				else
					return MessageFormat.format(pattern, key, size);
			}

			private String findSize(KeyValue from) {
				Object value = from.value;
				if (value instanceof Collection<?>)
					throw new IllegalStateException();
				else if (value instanceof Map<?, ?>) {
					int i = 0;
					for (Entry<?, ?> entry : ((Map<?, ?>) value).entrySet())
						if (entry.getValue() instanceof Map<?, ?>)
							i++;
					return Integer.toString(i);
				} else
					return Strings.nullSafeToString(from.value);
			}
		};
		IFunction1<KeyValue, Boolean> hideFn = new IFunction1<KeyValue, Boolean>() {
			@Override
			public Boolean apply(KeyValue from) throws Exception {
				return ignoredNamed.contains(from.key);
			}
		};
		final ImageRegistry imageRegistry = new ImageRegistry(display);
		new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);

		IFunction1<Map<String, Object>, Image> cardIconFn = new IFunction1<Map<String, Object>, Image>() {
			@Override
			public Image apply(Map<String, Object> from) throws Exception {
				Object object = from.get(CardConstants.slingResourceType);
				if (object == null)
					return imageRegistry.get(TitleAnchor.folderKey);
				if (object.equals(CardConstants.group))
					return imageRegistry.get(ArtifactsAnchor.organisationKey);
				else if (object.equals(CardConstants.artifact))
					return imageRegistry.get(ArtifactsAnchor.projectKey);
				else if (object.equals(CardConstants.version))
					return imageRegistry.get(ArtifactsAnchor.jarKey);
				else if (object.equals(CardConstants.versionJar))
					return imageRegistry.get(ArtifactsAnchor.jarKey);
				else
					return imageRegistry.get(TitleAnchor.folderKey);
			}
		};
		IFunction1<Map<String, Object>, Image> navIconFn = new IFunction1<Map<String, Object>, Image>() {
			@Override
			public Image apply(Map<String, Object> from) throws Exception {
				Object object = from.get("sling:resourceType");
				if (object == null)
					return null;// imageRegistry.get(TitleAnchor.folderKey);
				if (object.equals(CardConstants.group))
					return imageRegistry.get(TitleAnchor.groupKey);
				else if (object.equals(CardConstants.artifact))
					return imageRegistry.get(TitleAnchor.artifactKey);
				else if (object.equals(CardConstants.version))
					return imageRegistry.get(TitleAnchor.versionKey);
				else if (object.equals(CardConstants.versionJar))
					return imageRegistry.get(TitleAnchor.jarKey);
				else
					return null;
			}
		};

		String tag = resourceGetter.getStringOrNull("card.aggregator.tag");
		List<String> tagNames = Strings.splitIgnoreBlanks(tag, ",");
		String orderAsString = resourceGetter.getStringOrNull("card.order");
		String[] order = orderAsString.split(",");

		List<ICardDataModifier> modifiers = Arrays.asList(new CollectionsAggregatorModifier(CardConstants.slingResourceType), new FolderAggregatorModifier(CardConstants.jcrPrimaryType, CardConstants.ntUnstructured, CardConstants.slingResourceType), new KeyValueMissingItemsAdder(), new CardMapSorter());

		IDetailFactory detailFactory = IDetailFactory.Utils.detailsFactory(//
				new CollectionsDetailAdder(), //
				// new GroupDetailAdder(),//
				new CollectionItemDetailAdder(),//
				new ListDetailAdder(), //
				new TextEditorDetailAdder());
		IFunction1<ICard, String> defaultChildFn = new IFunction1<ICard, String>() {
			final Map<String, String> typeToDefaultChildMap = Maps.makeMap(//
					CardConstants.group, CardConstants.artifact,//
					CardConstants.artifact, CardConstants.version,//
					CardConstants.version, CardConstants.digest);

			@Override
			public String apply(ICard from) throws Exception {
				String cardType = typeToDefaultChildMap.get(from.cardType());
				Map<String, Object> data = from.data();
				if (data.containsKey(cardType))
					return cardType;
				if (data.containsKey("nt:unstructured"))
					return "nt:unstructured";
				return null;
			}
		};
		IFollowOnFragment followOnFragment = new IFollowOnFragment() {
			@Override
			public String findFollowOnFragment(String key, Object value) {
				if (value instanceof Map<?, ?>)
					return key;
				else
					return null;
			}
		};
		return config.withNameFn(nameFn).withValueFn(valueFn).withHideFn(hideFn).//
				withCardIconFn(cardIconFn).withResourceGetter(resourceGetter).withAggregatorTags(tagNames).//
				withNavIconFn(navIconFn).//
				withDetailsFactory(detailFactory).//
				withDefaultChildFn(defaultChildFn).//
				withSorter(Lists.orderedComparator(order)).//
				withKeyValueModifiers(modifiers).//
				withFollowOn(followOnFragment);
	}

	private String findKey(KeyValue from) {
		String key = from.key.replace(':', '_');
		return key;
	}

}
