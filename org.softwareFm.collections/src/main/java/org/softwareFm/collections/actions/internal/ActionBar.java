package org.softwareFm.collections.actions.internal;

import java.io.File;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.collections.actions.IActionBar;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.ExpressionData;
import org.softwareFm.jdtBinding.api.IExpressionCategoriser;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.softwareFmImages.actions.ActionAnchor;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class ActionBar implements IActionBar {

	private final static boolean newFeatures = true;

	private String urlKey;
	private final IExplorer explorer;
	private final CardConfig cardConfig;
	private State state;
	private BindingRipperResult ripperResult;
	private final IFunction1<BindingRipperResult, BindingRipperResult> reRipFn;

	private static enum State {
		URL, JUST_JAR, FROM_JAR, FROM_PATH;
	}

	public ActionBar(IExplorer explorer, CardConfig cardConfig, IFunction1<BindingRipperResult, BindingRipperResult> reRipFn) {
		this.explorer = explorer;
		this.cardConfig = cardConfig;
		this.reRipFn = reRipFn;
		this.urlKey = CardConstants.artifactUrlKey;
		this.state = State.FROM_JAR;
	}

	@Override
	public void selectionOccured(final BindingRipperResult ripperResult) {
		selectionOccured(ripperResult, true);
	}

	public void selectionOccured(final BindingRipperResult ripperResult, boolean showRadioStation) {
		this.ripperResult = ripperResult;
		if (ripperResult == null || ripperResult.path == null)
			return;
		switch (state) {
		case FROM_JAR:
			fromJarSelection(showRadioStation);
			break;
		case JUST_JAR:
			justJarSelection(showRadioStation);
			break;
		case FROM_PATH:// Will do snippet here
			fromPathSelection(showRadioStation);
			break;
		case URL:// Doesn't do anything...url selected
			break;
		default:
			throw new IllegalStateException(state.toString());
		}
	}

	IExpressionCategoriser categoriser = IExpressionCategoriser.Utils.categoriser();

	private void fromPathSelection(boolean showRadioStation) {
		final String url = makeUrlForSnippet(ripperResult);
		if (url != null)
			explorer.displayCard(url, new CardAndCollectionDataStoreAdapter() {
				@Override
				public void finished(ICardHolder cardHolder, String url, ICard card) {
					explorer.showRandomSnippetFor(url);
				}
			});
	}

	protected String makeUrlForSnippet(BindingRipperResult ripperResult) {
		ExpressionData key = categoriser.categorise(ripperResult.expression);
		if (key == null)
			return null;
		String baseUrl = CollectionConstants.rootUrl + "/" + key.classKey;
		if (key.methodKey != null && key.methodKey.length() > 0) {
			String result = baseUrl + "/method/" + key.methodKey;
			return result + "/snippet";
		} else
			return baseUrl + "/snippet";
	}

	private void justJarSelection(boolean showRadioStation) {
		String fileExtension = ripperResult.path.getFileExtension();
		if (!fileExtension.equals("jar")) {
			explorer.displayNotAJar();
			return;
		}
		final String hexDigest = ripperResult.hexDigest;
		IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarUrlKey);
		Map<String, Object> map = Maps.stringObjectMap(JdtConstants.hexDigestKey, hexDigest);
		String url = jarUrlGenerator.findUrlFor(map);
		if (url == null)
			throw new NullPointerException();
		explorer.displayCard(url, new CardAndCollectionDataStoreAdapter());
		if (showRadioStation)
			explorer.selectAndNext(url);
	}

	public void reselect() {
		selectionOccured(Functions.call(reRipFn, ripperResult), false);
	}

	private void fromJarSelection(final boolean showRadioStation) {
		String fileExtension = ripperResult.path.getFileExtension();
		if (!fileExtension.equals("jar")) {
			explorer.displayNotAJar();
			return;
		}
		final String hexDigest = ripperResult.hexDigest;
		IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarUrlKey);
		String jarUrl = jarUrlGenerator.findUrlFor(Maps.stringObjectMap(JdtConstants.hexDigestKey, hexDigest));

		cardConfig.cardDataStore.processDataFor(jarUrl, new ICardDataStoreCallback<Void>() {
			@Override
			public Void process(String jarUrl, Map<String, Object> result) throws Exception {
				IUrlGenerator urlGenerator = cardConfig.urlGeneratorMap.get(urlKey);
				String url = urlGenerator.findUrlFor(result);
				if (url == null)
					throw new NullPointerException();
				explorer.displayCard(url, new CardAndCollectionDataStoreAdapter());
				if (showRadioStation)
					explorer.selectAndNext(url);
				return null;
			}

			@Override
			public Void noData(String url) throws Exception {
				if (ripperResult != null && ripperResult.path != null) {
					final String hexDigest = ripperResult.hexDigest;
					final String unknownJarUrl = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, CardConstants.webPageUnknownJarUrl);
					File file = ripperResult.path.toFile();
					if (Files.extension(file.toString()).equals("jar")) {
						explorer.displayUnrecognisedJar(file, hexDigest, ripperResult.javaProject.getElementName());
						explorer.processUrl(DisplayConstants.browserFeedType, unknownJarUrl);
					}
				}
				return null;
			}
		});
	}

	@Override
	public void populateToolbar(IToolBarManager toolBarManager) {
		IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
		toolBarManager.add(Swts.Actions.Action(resourceGetter, CollectionConstants.actionWelcomeTitle, ActionAnchor.class, CollectionConstants.actionWelcomeImage, new Runnable() {
			@Override
			public void run() {
				showUrl(CardConstants.webPageWelcomeUrl);
				explorer.onlyShowBrowser();
				state = State.URL;
			}
		}));
		if (newFeatures)
			toolBarManager.add(Swts.Actions.Action(resourceGetter, CollectionConstants.actionGroupTitle, ActionAnchor.class, CollectionConstants.actionGroupImage, new Runnable() {
				@Override
				public void run() {
					state = State.FROM_JAR;
					urlKey = CardConstants.groupUrlKey;
					reselect();
					showUrl(CardConstants.webPageGroupUrl);
				}
			}));
		Action artifactAction = Swts.Actions.Action(resourceGetter, CollectionConstants.actionArtifactTitle, ActionAnchor.class, CollectionConstants.actionArtifactImage, new Runnable() {
			@Override
			public void run() {
				state = State.FROM_JAR;
				urlKey = CardConstants.artifactUrlKey;
				reselect();
				showUrl(CardConstants.webPageArtifactUrl);
			}
		});
		artifactAction.setChecked(true);
		toolBarManager.add(artifactAction);
		toolBarManager.add(Swts.Actions.Action(resourceGetter, CollectionConstants.actionVersionTitle, ActionAnchor.class, CollectionConstants.actionVersionImage, new Runnable() {
			@Override
			public void run() {
				state = State.FROM_JAR;
				urlKey = CardConstants.versionUrlKey;
				reselect();
				showUrl(CardConstants.webPageVersionUrl);
			}
		}));
		toolBarManager.add(Swts.Actions.Action(resourceGetter, CollectionConstants.actionJarTitle, ActionAnchor.class, CollectionConstants.actionJarImage, new Runnable() {
			@Override
			public void run() {
				state = State.JUST_JAR;
				urlKey = CardConstants.jarUrlKey;
				reselect();
				showUrl(CardConstants.webPageJarUrl);
			}
		}));
		if (newFeatures)
			toolBarManager.add(Swts.Actions.Action(resourceGetter, CollectionConstants.actionSnippetTitle, ActionAnchor.class, CollectionConstants.actionSnippetImage, new Runnable() {
				@Override
				public void run() {
					state = State.FROM_PATH;
					reselect();
					showUrl(CardConstants.webPageSnippetUrl);
				}
			}));
	}

	private void showUrl(String url) {
		String welcomeUrl = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, url);
		explorer.processUrl(DisplayConstants.browserFeedType, welcomeUrl);
	}
}
