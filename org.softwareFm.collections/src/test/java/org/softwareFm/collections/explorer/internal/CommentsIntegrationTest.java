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
			}
		});
	}

	public void testSomeMore() {
		fail();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		repository.post(Urls.compose(rootUrl, artifactUrl, CollectionConstants.comment), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), IResponseCallback.Utils.noCallback()).get();
		repository.post(Urls.compose(rootUrl, artifactUrl, CollectionConstants.comment, "someGuid"), Maps.stringObjectMap(//
				CollectionConstants.commentsTitleKey, "title1",//
				CollectionConstants.commentsTextKey, "text1", //
				CollectionConstants.createdTimeKey, 123), //
				IResponseCallback.Utils.noCallback()).get();

		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
	}
}
