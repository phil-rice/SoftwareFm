/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.eclipse.comments.ICommentsReader;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.comments.Comments;
import org.softwareFm.swt.comments.Comments.CommentsComposite;
import org.softwareFm.swt.menu.ICardMenuItemHandler;
import org.springframework.jdbc.core.JdbcTemplate;

public class CommentsIntegrationTest extends AbstractExplorerIntegrationTest {
	private final String key = Crypto.makeKey();
	private final String softwareFmId = "softwareFmId";

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
				CommentsComposite commentControl = (CommentsComposite) comments.getControl();
				assertSame(socialContent, commentControl);
				assertEquals("Comments for ant", commentControl.getTitle().getText());
				checkTable(comments.getTable(), 0, 0, "someMoniker", "Global", "globalComments/comment1");
				assertEquals(1, comments.getTable().getItemCount());
			}
		});
	}

	@Override
	protected ICommentsReader makeCommentsReader() {
		return ICommentsReader.Utils.mockReader(softwareFmId, "someMoniker", 1000l, "comment1");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.update("delete from users");
		template.update("insert into users (softwarefmid,crypto) values (?,?)", softwareFmId, key);
		makeServerUser().setUserProperty(softwareFmId, key, LoginConstants.monikerKey, "someMoniker");// creates user

		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
	}
}