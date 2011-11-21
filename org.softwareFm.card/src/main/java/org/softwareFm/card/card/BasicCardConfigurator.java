package org.softwareFm.card.card;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.card.internal.CardPopupMenuContributor;
import org.softwareFm.card.card.internal.RightClickCategoriser;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.IFollowOnFragment;
import org.softwareFm.card.details.IDetailFactory;
import org.softwareFm.card.details.internal.CollectionItemDetailAdder;
import org.softwareFm.card.details.internal.CollectionsDetailAdder;
import org.softwareFm.card.details.internal.EditorDetailAdder;
import org.softwareFm.card.details.internal.ListDetailAdder;
import org.softwareFm.card.editors.IEditorDetailAdder;
import org.softwareFm.card.editors.internal.StyledTextEditorDetailAdder;
import org.softwareFm.card.editors.internal.TextEditorDetailAdder;
import org.softwareFm.card.editors.internal.UrlEditorDetailAdder;
import org.softwareFm.card.modifiers.internal.CardMapSorter;
import org.softwareFm.card.modifiers.internal.CollectionsAggregatorModifier;
import org.softwareFm.card.modifiers.internal.FolderAggregatorModifier;
import org.softwareFm.card.modifiers.internal.KeyValueMissingItemsAdder;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.data.IUrlGeneratorMap;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.softwareFmImages.BasicImageRegisterConfigurator;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class BasicCardConfigurator implements ICardConfigurator {

	@Override
	public CardConfig configure(final Display display, CardConfig config) {
		final IResourceGetter baseResourceGetter = IResourceGetter.Utils.noResources().with(CardConfig.class, "Card");

		IFunction1<String, IEditorDetailAdder> editorFn = new IFunction1<String, IEditorDetailAdder>() {
			@Override
			public IEditorDetailAdder apply(String editorName) throws Exception {
				if (editorName.equals("text"))
					return new TextEditorDetailAdder();
				if (editorName.equals("url"))
					return new UrlEditorDetailAdder();
				else if (editorName.equals("styledText"))
					return new StyledTextEditorDetailAdder();
				else if (editorName.equals("none"))
					return null;
				throw new IllegalStateException(editorName);
			}
		};

		final IFunction1<String, IResourceGetter> resourceGetterFn = new IFunction1<String, IResourceGetter>() {
			private final Map<String, IResourceGetter> cache = Maps.newMap();

			@Override
			public IResourceGetter apply(final String from) throws Exception {
				return Maps.findOrCreate(cache, from, new Callable<IResourceGetter>() {
					@Override
					public IResourceGetter call() throws Exception {
						try {
							return baseResourceGetter.with(CardConfig.class, from);
						} catch (Exception e) {
							e.printStackTrace();
							return baseResourceGetter;
						}
					}
				});
			}
		};

		String keysToHide = baseResourceGetter.getStringOrNull("keys.hide");
		final Set<String> ignoredNamed = keysToHide == null ? Collections.<String> emptySet() : new HashSet<String>(Arrays.asList(keysToHide.split(",")));
		IFunction1<LineItem, String> nameFn = new IFunction1<LineItem, String>() {
			@SuppressWarnings("unchecked")
			@Override
			public String apply(LineItem from) throws Exception {
				if (from.value instanceof Map<?, ?>) {
					Map<Object, Object> map = (Map<Object, Object>) from.value;
					Object resourceType = map.get(CardConstants.slingResourceType);
					if (CardConstants.group.equals(resourceType))
						return from.key;
				}
				String key = findKey(from);
				String prettyKey = Strings.camelCaseToPretty(from.key);
				String pattern = IResourceGetter.Utils.get(resourceGetterFn, from.cardType, key + ".name");
				if (pattern == null)
					return prettyKey;
				else
					return MessageFormat.format(pattern, key, prettyKey);
			}
		};
		IFunction1<LineItem, String> valueFn = new IFunction1<LineItem, String>() {
			@Override
			public String apply(LineItem from) throws Exception {
				String key = findKey(from);
				String pattern = IResourceGetter.Utils.get(resourceGetterFn, from.cardType, key + ".value");
				String size = findSize(from);
				if (pattern == null)
					if (from.value instanceof Map<?, ?>)
						return size;
					else
						return Strings.nullSafeToString(from.value);
				else
					return MessageFormat.format(pattern, key, size);
			}

			private String findSize(LineItem from) {
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
		IFunction1<LineItem, Boolean> hideFn = new IFunction1<LineItem, Boolean>() {
			@Override
			public Boolean apply(LineItem from) throws Exception {
				return ignoredNamed.contains(from.key);
			}
		};
		final ImageRegistry imageRegistry = new ImageRegistry(display);
		new BasicImageRegisterConfigurator().registerWith(display, imageRegistry);
		IFunction1<ICard, TitleSpec> titleSpecFn = new IFunction1<ICard, TitleSpec>() {

			@Override
			public TitleSpec apply(ICard from) throws Exception {
				return makeTitleSpec(CardConstants.cardColorKey, CardConstants.indentTitleKey, CardConstants.cardTitleIcon, from);
			}

			private TitleSpec makeTitleSpec(String colorKey, String indentKey, String iconKey, ICard card) {
				Color color = Swts.makeColor(display, Functions.call(card.cardConfig().resourceGetterFn, card.cardType()), colorKey);
				int indent = IResourceGetter.Utils.getIntegerOrException(card.cardConfig().resourceGetterFn, card.cardType(), indentKey);
				String imageName = IResourceGetter.Utils.getOrException(card.cardConfig().resourceGetterFn, card.cardType(), iconKey);
				Image icon = imageRegistry.get(imageName);
				return new TitleSpec(icon, color, indent);
			}
		};

		IFunction1<Map<String, Object>, Image> navIconFn = new IFunction1<Map<String, Object>, Image>() {
			@Override
			public Image apply(Map<String, Object> from) throws Exception {
				String cardType = (String) from.get("sling:resourceType");
				String iconName = IResourceGetter.Utils.getOrNull(resourceGetterFn, cardType, CardConstants.navIcon);
				return imageRegistry.get(iconName);
			}
		};

		String tag = baseResourceGetter.getStringOrNull("card.aggregator.tag");
		List<String> tagNames = Strings.splitIgnoreBlanks(tag, ",");

		List<ICardDataModifier> modifiers = Arrays.asList(new CollectionsAggregatorModifier(CardConstants.slingResourceType), new FolderAggregatorModifier(CardConstants.jcrPrimaryType, CardConstants.ntUnstructured, CardConstants.slingResourceType), new KeyValueMissingItemsAdder(), new CardMapSorter(CardConstants.version));

		IDetailFactory detailFactory = IDetailFactory.Utils.detailsFactory(//
				new CollectionsDetailAdder(), //
				// new GroupDetailAdder(),//
				new CollectionItemDetailAdder(),//
				new ListDetailAdder(), //
				new EditorDetailAdder());
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
				for (Entry<String, Object> entry : data.entrySet())
					if (entry.getValue() instanceof Map<?, ?>) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) entry.getValue();
						if (CardConstants.group.equals(map.get(CardConstants.slingResourceType)))
							return entry.getKey();
					}
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
		IFunction1<String, Image> imageFn = new IFunction1<String, Image>() {
			@Override
			public Image apply(String from) throws Exception {
				return imageRegistry.get(from);
			}
		};
		IRightClickCategoriser rightClickCategoriser = new RightClickCategoriser();
		String prefix = "/softwareFm/data/";

		final IUrlGeneratorMap urlGeneratorMap = makeUrlGeneratorMap(prefix);
		final IPopupMenuContributor<ICard> contributor = new CardPopupMenuContributor();

		return config.withNameFn(nameFn).withValueFn(valueFn).withHideFn(hideFn).//
				withResourceGetterFn(resourceGetterFn).withAggregatorTags(tagNames).//
				withNavIconFn(navIconFn).//
				withDetailsFactory(detailFactory).//
				withDefaultChildFn(defaultChildFn).//
				withKeyValueModifiers(modifiers).//
				withFollowOn(followOnFragment).//
				withRightClickCategoriser(rightClickCategoriser).//
				withTitleSpecFn(titleSpecFn).//
				withUrlGeneratorMap(urlGeneratorMap).//
				withImageFn(imageFn).withPopupMenuContributor(contributor).//
				withEditorFn(editorFn);
	}

	public static IUrlGeneratorMap makeUrlGeneratorMap(String prefix) {
		final IUrlGeneratorMap urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap(//
				CardConstants.urlGroupKey, new UrlGenerator(prefix + "{3}/{2}", "groupId"),// hash, hash, groupId, groundIdWithSlash
				CardConstants.artifactUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}", "groupId", "artifactId"),// 0,1: hash, 2,3: groupId, 4,5: artifactId
				CardConstants.versionUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}/version/{6}", "groupId", "artifactId", "version"),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version
				CardConstants.digestUrlKey, new UrlGenerator(prefix + "{3}/{2}/artifact/{4}/version/{6}/digest/{8}", "groupId", "artifactId", "version", CardConstants.digest),// 0,1: hash, 2,3: groupId, 4,5: artifactId, 6,7: version, 8,9: digest
				CardConstants.jarUrlKey, new UrlGenerator("/softwareFm/jars/{0}/{1}/{2}", CardConstants.digest),// 0,1: hash, 2,3: digest
				CardConstants.userUrlKey, new UrlGenerator("/softwareFm/users/guid/{0}/{1}/{2}", "notSure"));// hash and guid
		return urlGeneratorMap;
	}

	private String findKey(LineItem from) {
		String key = from.key.replace(':', '_');
		return key;
	}

}
