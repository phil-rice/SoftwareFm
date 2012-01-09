package org.softwareFm.collections.explorer.internal;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.comments.Comments;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Urls;

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
		super.setUp();
		repository.post(Urls.compose(rootArtifactUrl, artifactUrl, CollectionConstants.comment), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), IResponseCallback.Utils.noCallback()).get();
		repository.post(Urls.compose(rootArtifactUrl, artifactUrl, CollectionConstants.comment, "someGuid1"), Maps.stringObjectMap(//
				CollectionConstants.commentsTitleKey, "title1",//
				CollectionConstants.commentsTextKey, "text1", //
				CollectionConstants.createdTimeKey, 123), //
				IResponseCallback.Utils.noCallback()).get();
		repository.post(Urls.compose(rootArtifactUrl, artifactUrl, CollectionConstants.comment, "someGuid2"), Maps.stringObjectMap(//
				CollectionConstants.commentsTitleKey, "title2",//
				CollectionConstants.commentsTextKey, "text2", //
				CollectionConstants.createdTimeKey, 124), //
				IResponseCallback.Utils.noCallback()).get();

		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
	}
}
