package org.softwareFm.card.internal;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardConfigurator;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class BasicCardConfigurator implements ICardConfigurator {

	@Override
	public CardConfig configure(Display display, CardConfig config) {
		final IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(CardConfig.class, "Card");
		String keysToHide = resourceGetter.getStringOrNull("keys.hide");
		final Set<String> ignoredNamed = keysToHide == null ? Collections.<String> emptySet() : new HashSet<String>(Arrays.asList(keysToHide.split(",")));
		IFunction1<KeyValue, String> nameFn = new IFunction1<KeyValue, String>() {
			@Override
			public String apply(KeyValue from) throws Exception {
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
				int size = from.value instanceof List ? ((List<?>) from.value).size() : 0;
				if (pattern == null)
					return Strings.nullSafeToString(from.value);
				else
					return MessageFormat.format(pattern, key, size);
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
				if (from.containsKey("name"))
					return imageRegistry.get(ArtifactsAnchor.projectKey);
				else if (from.containsKey("version"))
					return imageRegistry.get(ArtifactsAnchor.jarKey);
				else
					return imageRegistry.get(ArtifactsAnchor.organisationKey);
			}
		};

		String tag = resourceGetter.getStringOrNull("card.aggregator.tag");
		String orderAsString= resourceGetter.getStringOrNull("card.order");
		String[] order = orderAsString.split(",");
		return config.withNameFn(nameFn).withValueFn(valueFn).withHideFn(hideFn).//
				withCardIconFn(cardIconFn).withResourceGetter(resourceGetter).withAggregatorTag(tag).withSorter(KeyValue.Utils.orderedKeyComparator(order));
	}

	private String findKey(KeyValue from) {
		String key = from.key.replace(':', '_');
		return key;
	}

}
