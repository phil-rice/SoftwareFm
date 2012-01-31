package org.softwareFm.collections.explorer.internal;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.comments.Comments;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.utilities.maps.Maps;

public class CommentsIntegrationTest extends AbstractExplorerIntegrationTest {

	public void testCommentsAreNotShownInitiallyUntilATextItemIsSelected() {
		postArtifactData();
		displayCard(artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				assertNotSame(masterDetailSocial.getSocialContent(), explorer.getComments().getControl());
				selectItemAndNotifyListeners(card, "Name");
				dispatchUntilQueueEmpty();
				Control socialContent = masterDetailSocial.getSocialContent();
				Comments comments = explorer.getComments();
				Control commentControl = comments.getControl();
				assertSame(socialContent, commentControl);
				assertEquals("Comments for ant", comments.getTitle().getText());
				checkTable(comments.getTable(), 0, "title2", "text2");
				checkTable(comments.getTable(), 1, "title1", "text1");
				assertEquals(2, comments.getTable().getItemCount());
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		
//		Logger.getLogger(IGitLocal.class).setLevel(Level.DEBUG);
		Logger.getLogger(IHttpClient.class).setLevel(Level.DEBUG);
		super.setUp();
		gitLocal.put(IFileDescription.Utils.compose(rootArtifactUrl, artifactUrl, CollectionConstants.comment), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection));
		gitLocal.put(IFileDescription.Utils.compose(rootArtifactUrl, artifactUrl, CollectionConstants.comment, "someGuid1"), Maps.stringObjectMap(//
				CollectionConstants.commentsTitleKey, "title1",//
				CollectionConstants.commentsTextKey, "text1", //
				CollectionConstants.createdTimeKey, 123));
		gitLocal.put(IFileDescription.Utils.compose(rootArtifactUrl, artifactUrl, CollectionConstants.comment, "someGuid2"), Maps.stringObjectMap(//
				CollectionConstants.commentsTitleKey, "title2",//
				CollectionConstants.commentsTextKey, "text2", //
				CollectionConstants.createdTimeKey, 124));

		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
	}
}
