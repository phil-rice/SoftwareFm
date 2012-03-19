/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.membership.internal.UserMembershipReaderForLocal;
import org.softwareFm.crowdsource.server.callProcessor.internal.CommentProcessor;
import org.softwareFm.crowdsource.user.internal.GroupsForServer;
import org.softwareFm.crowdsource.user.internal.UserMembershipForServer;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.tests.IIntegrationTest;
import org.softwareFm.crowdsource.utilities.tests.INeedsServerTest;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.eclipse.mysoftwareFm.IGroupClientOperations;
import org.softwareFm.eclipse.mysoftwareFm.MyDetails;
import org.softwareFm.eclipse.mysoftwareFm.MyGroups;
import org.softwareFm.eclipse.mysoftwareFm.MyPeople;
import org.softwareFm.eclipse.mysoftwareFm.RequestGroupReportGeneration;
import org.softwareFm.eclipse.snippets.SnippetFeedConfigurator;
import org.softwareFm.eclipse.user.ProjectTimeGetterFixture;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;
import org.softwareFm.jarAndClassPath.api.IRequestGroupReportGeneration;
import org.softwareFm.jarAndClassPath.api.SoftwareFmServer;
import org.softwareFm.swt.ICollectionConfigurationFactory;
import org.softwareFm.swt.browser.IBrowserConfigurator;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.ILineSelectedListener;
import org.softwareFm.swt.comments.ICommentWriter;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.explorer.ExplorerAdapter;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.explorer.IUserDataManager;
import org.softwareFm.swt.explorer.internal.Explorer;
import org.softwareFm.swt.explorer.internal.MasterDetailSocial;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.swt.SwtAndServiceTest;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.timeline.IPlayListGetter;

/** These tests go out to software fm, so they are much more fragile */
abstract public class AbstractExplorerIntegrationTest extends SwtAndServiceTest implements INeedsServerTest, IIntegrationTest {

	final static String groupUrl = "/ant";
	final static String artifactUrl = "/ant/ant/artifact/ant";
	final static String snippetrepoUrl = "/java/io/File";
	final static String snippetUrl = "/snippet/java/io/File/snippet";
	
	protected final String groupId1 = "groupId1";
	protected final String groupId2 = "groupId2";

	protected final String groupCryptoKey1 = Crypto.makeKey();
	protected final String groupCryptoKey2 = Crypto.makeKey();
	
	protected CardConfig cardConfig;
	protected IGitLocal gitLocal;
	public Explorer explorer;
	protected IHttpClient httpClient;
	protected MasterDetailSocial masterDetailSocial;

	protected final String prefix = "/tests/" + getClass().getSimpleName();
	protected final String rootArtifactUrl = prefix + "/data";
	protected final String rootSnippetUrl = prefix + "/snippet";
	protected IResourceGetter rawResourceGetter;
	private File root;
	protected File localRoot;
	protected File remoteRoot;
	protected String userCryptoKey = Crypto.makeKey();
	protected String userCryptoKey1 = Crypto.makeKey();
	protected String userCryptoKey2 = Crypto.makeKey();
	protected String userCryptoKey3 = Crypto.makeKey();
	private ICrowdSourcedServer crowdSourcedServer;
	private IShowMyData showMyData;
	protected IUserReader userReader;
	protected IUserMembershipReader userMembershipReader;
	protected LocalGroupsReader groupsReader;
	protected ICommentWriter commentsWriter;
	protected ICommentsReader commentsReader;
	protected BasicDataSource dataSource;
	protected IUserDataManager userDataManager;
	private IGroupClientOperations groupClientOperations;
	protected IUser user;
	protected ProcessCallParameters processCallParameters;
	protected IUrlGenerator userUrlGenerator;
	protected IUrlGenerator groupUrlGenerator;
	private IGitOperations remoteOperations;
	protected GroupsForServer groups;
	protected UserMembershipForServer membershipForServer;
	protected IFunction1<String, String> repoDefnFn;
	protected MailerMock mailer;
	private Callable<String> userCryptoGenerator;
	private Callable<String> softwareFmIdGenerator;
	private IFunction1<Map<String, Object>, String> cryptoFn;

	public static interface CardHolderAndCardCallback {
		void process(ICardHolder cardHolder, ICard card) throws Exception;
	}

	static interface IAddingCallback<T> {
		/** will get called twice. If added is false, card is the initial card, if false card is the added card */
		void process(boolean added, T data, IAdding adding);
	}

	static interface IAdding {
		void tableItem(int index, String name, String existing, String newValue);
	}

	protected void displayCard(final String url, final CardHolderAndCardCallback cardHolderAndCardCallback) {
		doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {
			@Override
			public void run() {
				explorer.displayCard(rootArtifactUrl + url, new CardAndCollectionDataStoreAdapter());
			}
		}, cardHolderAndCardCallback);

	}

	protected void displayCardThenViewChild(String url, final String childTitle, final CardHolderAndCardCallback callback) {
		final AtomicInteger count = new AtomicInteger();
		displayCard(url, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				System.out.println("in displaceCardThenViewChild");
				final CountDownLatch latch = new CountDownLatch(1);
				cardHolder.addLineSelectedListener(new ILineSelectedListener() {
					@Override
					public void selected(ICard card, String key, Object value) {
						if (latch.getCount() == 0)
							fail("Card: " + card + "\nKey: " + key + " value: " + value);
						latch.countDown();
					}
				});
				Menu menu = selectAndCreatePopupMenu(card, childTitle);
				executeMenuItem(menu, "View");
				// this should have called "explorer.showContents
				// Now wait for the menu to have "done it's thing"
				dispatchUntilTimeoutOrLatch(latch, CommonConstants.testTimeOutMs);
				ICard childCard = cardHolder.getCard();
				callback.process(cardHolder, childCard);
				assertEquals(1, count.incrementAndGet());
			}

		});
		assertEquals(1, count.get());// check the body was called
	}

	protected ICard doSomethingAndWaitForCardDataStoreToFinish(Runnable something, final CardHolderAndCardCallback cardHolderAndCardCallback) {
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<ICard> cardRef = new AtomicReference<ICard>();
		final AtomicReference<Exception> exception = new AtomicReference<Exception>();
		explorer.addExplorerListener(new ExplorerAdapter() {
			@Override
			public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
				try {
					explorer.removeExplorerListener(this);
					cardHolderAndCardCallback.process(cardHolder, card);
					dispatchUntilQueueEmpty();
					cardRef.set(card);
				} catch (Exception e) {
					exception.set(e);
					throw e;
				} finally {
					latch.countDown();
				}
			}
		});
		something.run();
		dispatchUntilTimeoutOrLatch(latch, CommonConstants.testTimeOutMs);
		if (exception.get() != null)
			throw new RuntimeException(exception.get());
		return cardRef.get();
	}

	protected void executeMenuItem(Menu menu, String title) {
		for (MenuItem item : menu.getItems()) {
			if (item.getText().equals(title)) {
				item.notifyListeners(SWT.Selection, new Event());
				return;
			}
		}
		throw new IllegalArgumentException(title + "\n" + menu);
	}

	protected Menu selectAndCreatePopupMenu(ICard card, String title) {
		selectItem(card, title);
		return createPopupMenu(card);
	}

	protected Menu createPopupMenu(ICard card) {
		final Menu menu = new Menu(shell);
		String popupMenuId = Functions.call(card.getCardConfig().popupMenuIdFn, card);
		cardConfig.popupMenuService.contributeTo(popupMenuId, new Event(), menu, card);
		return menu;
	}

	protected void selectItemAndNotifyListeners(ICard card, String title) {
		selectItem(card, title);
		card.getTable().notifyListeners(SWT.Selection, new Event());
	}

	protected void selectItem(ICard card, String title) {
		Table table = card.getTable();
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			if (item.getText(0).equals(title)) {
				table.select(i);
				return;
			}
		}
		Assert.fail("Cannot find: " + title + "\nCard: " + card);
	}

	protected void checkTable(Table table, int i, String name, String value) {
		assertEquals(name, table.getItem(i).getText(0));
		assertEquals(value, table.getItem(i).getText(1));

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		root = Tests.makeTempDirectory(getClass().getSimpleName());
		localRoot = new File(root, "local");
		remoteRoot = new File(root, "remote");
		String remoteAsUri = new File(root, "remote").getAbsolutePath();
		repoDefnFn = Strings.firstNSegments(3);

		httpClient = IHttpClient.Utils.builder("localhost", CommonConstants.testPort);

		gitLocal = IGitLocal.Utils.localReader(//
				new HttpRepoFinder(httpClient, CommonConstants.testTimeOutMs), //
				IGitOperations.Utils.gitOperations(localRoot), //
				new HttpGitWriter(httpClient, CommonConstants.testTimeOutMs), remoteAsUri, CommonConstants.staleCachePeriodForTest);
		dataSource = AbstractLoginDataAccessor.defaultDataSource();

		userCryptoGenerator = Callables.valueFromList(userCryptoKey, userCryptoKey1, userCryptoKey2, userCryptoKey3);
		softwareFmIdGenerator = Callables.patternWithCount("newSoftwareFmId{0}");

		cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().//
				configure(display, new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(shell, service, gitLocal))).//
				withUrlGeneratorMap(ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(prefix, "data"));

		remoteOperations = IGitOperations.Utils.gitOperations(remoteRoot);
		cryptoFn = ICrowdSourcedServer.Utils.cryptoFn(dataSource);
		Map<String, Callable<Object>> defaultProperties = SoftwareFmServer.makeUserDefaultProperties();
		mailer = new MailerMock();
		processCallParameters = new ProcessCallParameters(dataSource, remoteOperations, userCryptoGenerator, softwareFmIdGenerator, cryptoFn, mailer, defaultProperties, prefix);
		user = processCallParameters.user;
		userUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.userUrlKey);
		groupUrlGenerator = GroupConstants.groupsGenerator(prefix);
		groupsReader = new LocalGroupsReader(groupUrlGenerator, gitLocal);
		groups = new GroupsForServer(groupUrlGenerator, remoteOperations, repoDefnFn);
		membershipForServer = new UserMembershipForServer(userUrlGenerator, user, remoteOperations, repoDefnFn);

		userReader = makeUserReader();
		userMembershipReader = new UserMembershipReaderForLocal(userUrlGenerator, gitLocal, userReader);
		IProcessCall[] extraProcessCalls = getExtraProcessCalls(remoteOperations, cryptoFn);
		IProcessCall processCall = IProcessCall.Utils.softwareFmProcessCall(processCallParameters, Functions.<ProcessCallParameters, IProcessCall[]> constant(extraProcessCalls));
		crowdSourcedServer = ICrowdSourcedServer.Utils.testServerPort(processCall, ICallback.Utils.rethrow());
		try {

			rawResourceGetter = cardConfig.resourceGetterFn.apply(null);
			masterDetailSocial = new MasterDetailSocial(shell, SWT.NULL);
			IProjectTimeGetter projectTimeGetter = new ProjectTimeGetterFixture();
			showMyData = MyDetails.showMyDetails(service, cardConfig, masterDetailSocial, userUrlGenerator, gitLocal, projectTimeGetter);

			IRequestGroupReportGeneration reportGenerator = new RequestGroupReportGeneration(httpClient, IResponseCallback.Utils.sysoutStatusCallback(), gitLocal);
			groupClientOperations = IGroupClientOperations.Utils.groupOperations(masterDetailSocial, cardConfig, httpClient);
			IShowMyGroups showMyGroups = MyGroups.showMyGroups(service, false, cardConfig, masterDetailSocial, userUrlGenerator, groupUrlGenerator, gitLocal, projectTimeGetter, reportGenerator, groupClientOperations);
			IShowMyPeople showMyPeople = MyPeople.showMyPeople(service, masterDetailSocial, cardConfig, gitLocal, userUrlGenerator, groupUrlGenerator, projectTimeGetter, reportGenerator, CommonConstants.testTimeOutMs * 10);

			commentsWriter = ICommentWriter.Utils.commentWriter(httpClient, CommonConstants.testTimeOutMs, gitLocal);
			commentsReader = makeCommentsReader();
			userDataManager = IUserDataManager.Utils.userDataManager();
			explorer = (Explorer) IExplorer.Utils.explorer(masterDetailSocial, userReader, userMembershipReader, groupsReader, cardConfig, //
					Arrays.asList(rootArtifactUrl, rootSnippetUrl), //
					IPlayListGetter.Utils.noPlayListGetter(), service, //
					ILoginStrategy.Utils.noLoginStrategy(),//
					showMyData, showMyGroups, showMyPeople,//
					userDataManager, commentsWriter, commentsReader, Callables.<Long> exceptionIfCalled());
			IBrowserConfigurator.Utils.configueWithUrlRssTweet(explorer);
			new SnippetFeedConfigurator(cardConfig.cardDataStore, cardConfig.resourceGetterFn).configure(explorer);
			// SnippetFeedConfigurator.configure(explorer, cardConfig);
			httpClient.delete(Urls.compose(rootArtifactUrl, artifactUrl)).execute(IResponseCallback.Utils.noCallback()).get();
			httpClient.delete(Urls.compose(rootArtifactUrl, snippetUrl)).execute(IResponseCallback.Utils.noCallback()).get();
			gitLocal.init(Urls.compose(rootArtifactUrl, groupUrl));
		} catch (Exception e) {
			crowdSourcedServer.shutdown();
			e.printStackTrace();
			throw e;
		}
	}

	protected IProcessCall[] getExtraProcessCalls(IGitOperations remoteGitOperations, IFunction1<Map<String, Object>, String> cryptoFn) {
		return new IProcessCall[] {//
				new CommentProcessor(userReader, userMembershipReader, groupsReader, new CommentsForServer(remoteGitOperations, user, userMembershipReader, groupsReader, Callables.value(1000l)), cryptoFn) };
	}

	protected IFunction1<String, String> getEmailToSfmIdFn() {
		return Functions.expectionIfCalled();
	}

	protected IUserReader makeUserReader() {
		return IUserReader.Utils.exceptionUserReader();
	}

	protected ICommentsReader makeCommentsReader() {
		return new CommentsReaderLocal(gitLocal, userReader, userMembershipReader, groupsReader);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			super.tearDown();
			masterDetailSocial.dispose();
			explorer.dispose();
			cardConfig.dispose();
			dataSource.close();
		} finally {
			crowdSourcedServer.shutdown();
		}
		Tests.waitUntilCanDeleteTempDirectory(getClass().getSimpleName(), 2000);

	}

	protected void postSnippetData() {
		try {
			String url = Urls.compose(rootSnippetUrl, snippetUrl);
			gitLocal.put(IFileDescription.Utils.plain(url), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void postArtifactData() {
		gitLocal.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl)), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.artifact));
		gitLocal.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial")), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection));
		gitLocal.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial/one")), Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"));
		gitLocal.put(IFileDescription.Utils.plain(Urls.compose(rootArtifactUrl, artifactUrl, "/tutorial/two")), Maps.stringObjectMap(CardConstants.slingResourceType, "tutorial"));
	}

	protected void checkAndEdit(final Table cardTable, IAddingCallback<Table> addingCallback) {
		addingCallback.process(false, cardTable, new IAdding() {
			@Override
			public void tableItem(int index, String name, String existing, String newValue) {
				String prettyName = Strings.camelCaseToPretty(name);
				TableItem item = cardTable.getItem(index);
				assertEquals(prettyName, item.getText(0));
				assertEquals(existing, item.getText(1));
				cardTable.select(index);
				cardTable.notifyListeners(SWT.Selection, new Event());
				Text text = Swts.findChildrenWithClass(cardTable, Text.class).get(index);
				assertEquals(existing, text.getText());
				text.setText(newValue);
			}
		});
	}
}