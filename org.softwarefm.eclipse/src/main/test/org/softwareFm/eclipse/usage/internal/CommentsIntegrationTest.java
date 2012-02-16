/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.comments.Comments;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.menu.ICardMenuItemHandler;

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