package org.softwareFm.collections.actions.internal;

import java.io.File;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.collections.actions.IActionBar;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.display.constants.DisplayConstants;
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
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.url.IUrlGenerator;

public class ActionBar implements IActionBar {

	private final static boolean newFeatures = false;

	private String urlKey;
	private final IExplorer explorer;
	private final CardConfig cardConfig;
	private State state;
	private BindingRipperResult ripperResult;
	private final IFunction1<BindingRipperResult, BindingRipperResult> reRipFn;
	private IAfterDisplayCard afterDisplayCard = IAfterDisplayCard.Utils.noCallback();

	private final boolean admin;

	static enum State {
		URL, JUST_JAR, FROM_JAR, FROM_PATH, DEBUG;
	}

	public ActionBar(IExplorer explorer, CardConfig cardConfig, IFunction1<BindingRipperResult, BindingRipperResult> reRipFn, boolean admin) {
		this.explorer = explorer;
		this.cardConfig = cardConfig;
		this.reRipFn = reRipFn;
		this.admin = admin;
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
		case DEBUG:
			debug();
			break;
		default:
			throw new IllegalStateException(state.toString());
		}
	}

	private void debug() {
		explorer.showDebug(ripperResult.toString());
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
		String baseUrl = CollectionConstants.rootUrl + "/snippet/" + key.classKey;
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
		System.out.println("Processing JarUrl: " + jarUrl);
		cardConfig.cardDataStore.processDataFor(jarUrl, new ICardDataStoreCallback<Void>() {
			@Override
			public Void process(String jarUrl, final Map<String, Object> groupArtifactVersionMap) throws Exception {
				String groupId = (String) groupArtifactVersionMap.get(CollectionConstants.groupId);
				String artifactId = (String) groupArtifactVersionMap.get(CollectionConstants.artifactId);
				IUrlGenerator urlGenerator = cardConfig.urlGeneratorMap.get(urlKey);
				String url = urlGenerator.findUrlFor(groupArtifactVersionMap);
				if (url == null)
					throw new NullPointerException();
				explorer.displayCard(url, new CardAndCollectionDataStoreAdapter() {
					@Override
					public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
						afterDisplayCard.process(card, groupArtifactVersionMap);
					}
				});
				if (showRadioStation)
					explorer.selectAndNext(url);
				explorer.usage(groupId, artifactId);
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
		toolBarManager.add(Swts.Actions.radioAction(resourceGetter, CollectionConstants.actionWelcomeTitle, ActionAnchor.class, CollectionConstants.actionWelcomeImage, new Runnable() {
			@Override
			public void run() {
				setState(State.URL);
				showUrl(CardConstants.webPageWelcomeUrl);
				explorer.showMySoftwareFm();
			}
		}));

		if (newFeatures)
			toolBarManager.add(Swts.Actions.radioAction(resourceGetter, CollectionConstants.actionGroupTitle, ActionAnchor.class, CollectionConstants.actionGroupImage, new Runnable() {
				@Override
				public void run() {
					setState(State.FROM_JAR);
					urlKey = CardConstants.groupUrlKey;
					reselect();
					showUrl(CardConstants.webPageGroupUrl);
				}
			}));
		Action artifactAction = Swts.Actions.radioAction(resourceGetter, CollectionConstants.actionArtifactTitle, ActionAnchor.class, CollectionConstants.actionArtifactImage, new Runnable() {
			@Override
			public void run() {
				setState(State.FROM_JAR);
				urlKey = CardConstants.artifactUrlKey;
				reselect();
				showUrl(CardConstants.webPageArtifactUrl);
			}
		});
		artifactAction.setChecked(true);
		toolBarManager.add(artifactAction);
		toolBarManager.add(Swts.Actions.radioAction(resourceGetter, CollectionConstants.actionVersionTitle, ActionAnchor.class, CollectionConstants.actionVersionImage, new Runnable() {
			@Override
			public void run() {
				setState(State.FROM_JAR);
				afterDisplayCard = new IAfterDisplayCard() {
					@Override
					public void process(ICard card, Map<String, Object> groupArtifactVersionMap) {
						String version = Strings.nullSafeToString(groupArtifactVersionMap.get(CollectionConstants.version));
						if (version != null) {
							Table table = card.getTable();
							for (TableItem item : table.getItems()) {
								Object data = item.getData();
								if (version.equals(data)) {
									table.setSelection(item);
									table.notifyListeners(SWT.Selection, new Event());
									return;
								}
							}
						}
					}
				};
				urlKey = CardConstants.versionCollectionUrlKey;
				reselect();
			}
		}));
		if (admin)
			toolBarManager.add(Swts.Actions.radioAction(resourceGetter, CollectionConstants.actionJarTitle, ActionAnchor.class, CollectionConstants.actionJarImage, new Runnable() {
				@Override
				public void run() {
					setState(State.JUST_JAR);
					urlKey = CardConstants.jarUrlKey;
					reselect();
					showUrl(CardConstants.webPageJarUrl);
				}
			}));
		if (admin)
			toolBarManager.add(Swts.Actions.radioAction(resourceGetter, CollectionConstants.actionDebugTitle, ActionAnchor.class, CollectionConstants.actionDebugImage, new Runnable() {
				@Override
				public void run() {
					setState(State.DEBUG);
					reselect();
					showUrl(CardConstants.webPageDebugUrl);
				}
			}));

		toolBarManager.add(Swts.Actions.radioAction(resourceGetter, CollectionConstants.actionSnippetTitle, ActionAnchor.class, CollectionConstants.actionSnippetImage, new Runnable() {
			@Override
			public void run() {
				reselect();
				showUrl(CardConstants.webPageSnippetUrl);
				setState(State.FROM_PATH);
			}
		}));
		toolBarManager.add(Swts.Actions.pushAction(resourceGetter, CollectionConstants.actionRefreshTitle, ActionAnchor.class, CollectionConstants.actionRefreshImage, new Runnable() {
			@Override
			public void run() {
				ICard card = explorer.getCardHolder().getCard();
				if (card != null) {
					String url = card.url();
					if (url != null) {
						cardConfig.cardDataStore.clearCache(url);
						explorer.displayCard(url, new CardAndCollectionDataStoreAdapter());
					}
				}
			}
		}));
		if (admin)
			toolBarManager.add(Swts.Actions.pushAction(resourceGetter, CollectionConstants.actionNukeTitle, ActionAnchor.class, CollectionConstants.actionNukeImage, new Runnable() {
				@Override
				public void run() {
					ICard card = explorer.getCardHolder().getCard();
					if (card != null) {
						String url = card.url();
						if (MessageDialog.openConfirm(explorer.getControl().getShell(), CollectionConstants.confirmDelete, url))
							cardConfig.cardDataStore.delete(url, new IAfterEditCallback() {
								@Override
								public void afterEdit(final String url) {
									Swts.asyncExec(explorer.getControl(), new Runnable() {
										@Override
										public void run() {
											explorer.displayCard(url, new CardAndCollectionDataStoreAdapter());
										}
									});
								}
							});
					}
				}
			}));
	}

	protected void setState(State state) {
		this.state = state;
		afterDisplayCard = IAfterDisplayCard.Utils.noCallback();

	}

	private void showUrl(String url) {
		String welcomeUrl = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, url);
		explorer.processUrl(DisplayConstants.browserFeedType, welcomeUrl);
	}
}
