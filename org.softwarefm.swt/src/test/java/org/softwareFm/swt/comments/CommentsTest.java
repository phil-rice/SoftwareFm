/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.comments;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.easymock.EasyMock;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.crowdsource.api.IApiBuilder;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.LocalConfig;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.eclipse.usage.internal.ApiAndSwtTest;
import org.softwareFm.jarAndClassPath.api.UserData;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.comments.Comments.CommentsComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.internal.IExplorerTest;
import org.softwareFm.swt.swt.Swts;

public class CommentsTest extends ApiAndSwtTest {

	private Comments comments;
	private CardConfig cardConfig;
	private ICommentsReader commentsReader;
	private ICommentsCallback commentsCallback;
	private Runnable addComment;

	private final Map<String, Object> comment1 = Maps.stringObjectMap(CommentConstants.creatorKey, "creator1", CommentConstants.timeKey, 1l, CommentConstants.textKey, "text1", CommentConstants.sourceKey, "source1");
	private final Map<String, Object> comment2 = Maps.stringObjectMap(CommentConstants.creatorKey, "creator2", CommentConstants.timeKey, 2l, CommentConstants.textKey, "text2", CommentConstants.sourceKey, "source2");
	private final Map<String, Object> comment3 = Maps.stringObjectMap(CommentConstants.creatorKey, "creator3", CommentConstants.timeKey, 3l, CommentConstants.textKey, "text3", CommentConstants.sourceKey, "source3");

	private final Map<String, Object> comment1S2T3 = Maps.stringObjectMap(CommentConstants.creatorKey, "creator1", CommentConstants.timeKey, 3l, CommentConstants.textKey, "text1", CommentConstants.sourceKey, "source2");
	private final Map<String, Object> comment1S3T2 = Maps.stringObjectMap(CommentConstants.creatorKey, "creator1", CommentConstants.timeKey, 2l, CommentConstants.textKey, "text1", CommentConstants.sourceKey, "source3");

	private final Map<String, Object> comment1S1T2 = Maps.stringObjectMap(CommentConstants.creatorKey, "creator1", CommentConstants.timeKey, 2l, CommentConstants.textKey, "text1", CommentConstants.sourceKey, "source1");
	private final Map<String, Object> comment1S1T3 = Maps.stringObjectMap(CommentConstants.creatorKey, "creator1", CommentConstants.timeKey, 3l, CommentConstants.textKey, "text1", CommentConstants.sourceKey, "source1");

	private final UserData loggedInUserData = new UserData("someEmail", "someSfmId", "someCrypto");

	public void testTitleShowsNoContentsAndBlankContentsWhenNoCommentsAndNotLoggedIn() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Collections.<Map<String, Object>> emptyList());
		replayMocks();
		comments.showCommentsFor(UserData.blank(), "artefact", "someUrl");
		Table table = comments.getTable();
		assertEquals(0, table.getItemCount());
		checkTitleAndTableColumnNames(table, "No comments");
	}

	public void testTitleShowsNoContentsAndBlankContentsWhenNoCommentsAndLoggedIn() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Collections.<Map<String, Object>> emptyList());
		EasyMock.expect(commentsReader.groupComments("someUrl", "someSfmId", "someCrypto")).andReturn(Collections.<Map<String, Object>> emptyList());
		EasyMock.expect(commentsReader.myComments("someUrl", "someSfmId", "someCrypto", CommentConstants.mySource)).andReturn(Collections.<Map<String, Object>> emptyList());
		replayMocks();
		comments.showCommentsFor(loggedInUserData, "artefact", "someUrl");
		Table table = comments.getTable();
		assertEquals(0, table.getItemCount());
		checkTitleAndTableColumnNames(table, "No comments");
	}

	@SuppressWarnings("unchecked")
	public void testShowsGlobalContentWhenNotLoggedIn() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Arrays.asList(comment1, comment2, comment3));
		replayMocks();
		comments.showCommentsFor(UserData.blank(), "artefact", "someUrl");
		Table table = comments.getTable();
		assertEquals(3, table.getItemCount());
		checkTitleAndTableColumnNames(table, "Comments");
		Swts.checkTable(table, 0, 0, "creator3", "source3", "text3");
		Swts.checkTable(table, 1, 1, "creator2", "source2", "text2");
		Swts.checkTable(table, 2, 2, "creator1", "source1", "text1");
	}

	@SuppressWarnings("unchecked")
	public void testShowsGlobalMyAndMyGroupsContentWhenLoggedIn() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Arrays.asList(comment1));
		EasyMock.expect(commentsReader.groupComments("someUrl", "someSfmId", "someCrypto")).andReturn(Arrays.asList(comment2));
		EasyMock.expect(commentsReader.myComments("someUrl", "someSfmId", "someCrypto", CommentConstants.mySource)).andReturn(Arrays.asList(comment3));
		replayMocks();
		comments.showCommentsFor(loggedInUserData, "artefact", "someUrl");
		Table table = comments.getTable();
		assertEquals(3, table.getItemCount());
		checkTitleAndTableColumnNames(table, "Comments");
		Swts.checkTable(table, 0, 0, "creator3", "source3", "text3");
		Swts.checkTable(table, 1, 1, "creator2", "source2", "text2");
		Swts.checkTable(table, 2, 2, "creator1", "source1", "text1");
	}

	@SuppressWarnings("unchecked")
	public void testAddCommentButtonDisabledWhenNotLoggedIn() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Arrays.asList(comment1, comment2, comment3));
		replayMocks();
		comments.showCommentsFor(UserData.blank(), "artefact", "someUrl");
		CommentsComposite composite = (CommentsComposite) comments.getControl();
		assertFalse(composite.getFooter().addCommentButton.isEnabled());
	}

	@SuppressWarnings("unchecked")
	public void testAddCommentButtonEnabledWhenLoggedIn() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Arrays.asList(comment1, comment2, comment3));
		EasyMock.expect(commentsReader.groupComments("someUrl", "someSfmId", "someCrypto")).andReturn(Collections.<Map<String, Object>> emptyList());
		EasyMock.expect(commentsReader.myComments("someUrl", "someSfmId", "someCrypto", CommentConstants.mySource)).andReturn(Collections.<Map<String, Object>> emptyList());

		replayMocks();
		comments.showCommentsFor(loggedInUserData, "artefact", "someUrl");
		CommentsComposite composite = (CommentsComposite) comments.getControl();
		assertTrue(composite.getFooter().addCommentButton.isEnabled());
	}

	@SuppressWarnings("unchecked")
	public void testAddRunableIsCalledWhenButtonClicked() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Arrays.asList(comment1, comment2, comment3));
		EasyMock.expect(commentsReader.groupComments("someUrl", "someSfmId", "someCrypto")).andReturn(Collections.<Map<String, Object>> emptyList());
		EasyMock.expect(commentsReader.myComments("someUrl", "someSfmId", "someCrypto", CommentConstants.mySource)).andReturn(Collections.<Map<String, Object>> emptyList());
		addComment.run();
		replayMocks();
		comments.showCommentsFor(loggedInUserData, "artefact", "someUrl");

		CommentsComposite composite = (CommentsComposite) comments.getControl();
		composite.getFooter().addCommentButton.notifyListeners(SWT.MouseUp, new Event());
		dispatchUntilQueueEmpty();
	}

	@SuppressWarnings("unchecked")
	public void testShowCommentCallbackIsCalledWhenTableLineIsSelected() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Arrays.asList(comment1));
		EasyMock.expect(commentsReader.groupComments("someUrl", "someSfmId", "someCrypto")).andReturn(Arrays.asList(comment2));
		EasyMock.expect(commentsReader.myComments("someUrl", "someSfmId", "someCrypto", CommentConstants.mySource)).andReturn(Arrays.asList(comment3));
		commentsCallback.selected("artefact", "someUrl", 1, comment2);
		replayMocks();

		comments.showCommentsFor(loggedInUserData, "artefact", "someUrl");
		Table table = comments.getTable();
		table.select(1);
		table.notifyListeners(SWT.Selection, new Event());
	}

	/** comment1,2 and 3 are checked earlier, and their source order and date order are in line */
	@SuppressWarnings("unchecked")
	public void testCommentsUseTimeOrderAsPrimary() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Arrays.asList(comment1S2T3, comment1S3T2, comment1));
		replayMocks();
		comments.showCommentsFor(UserData.blank(), "artefact", "someUrl");
		Table table = comments.getTable();
		assertEquals(3, table.getItemCount());
		checkTitleAndTableColumnNames(table, "Comments");
		Swts.checkTable(table, 0, 0, "creator1", "source2", "text1");
		Swts.checkTable(table, 1, 1, "creator1", "source3", "text1");
		Swts.checkTable(table, 2, 2, "creator1", "source1", "text1");
	}

	@SuppressWarnings("unchecked")
	public void testCommentsUseTimeOrder2() {
		EasyMock.expect(commentsReader.globalComments("someUrl", CommentConstants.globalSource)).andReturn(Arrays.asList(comment1S1T2, comment1S1T3, comment1));
		replayMocks();
		comments.showCommentsFor(UserData.blank(), "artefact", "someUrl");
		Table table = comments.getTable();
		assertEquals(3, table.getItemCount());
		checkTitleAndTableColumnNames(table, "Comments");
		Swts.checkTable(table, 0, 0, "creator1", "source1", "text1");
		Swts.checkTable(table, 1, 1, "creator1", "source1", "text1");
		Swts.checkTable(table, 2, 2, "creator1", "source1", "text1");
		CommentsComposite composite = (CommentsComposite) comments.getControl();
		assertEquals(Arrays.asList(comment1S1T3, comment1S1T2, comment1), composite.allComments);
	}

	public void testDealsWithCorruptedComments() {
		fail();
	}

	protected void checkTitleAndTableColumnNames(Table table, String expectedTitle) {
		CommentsComposite composite = (CommentsComposite) comments.getControl();
		assertEquals(expectedTitle, composite.getTitle().getText());
		Swts.checkTableColumns(table, "creatorTitle", "sourceTitle", "textTitle");
	}

	private void replayMocks() {
		EasyMock.replay(commentsReader, commentsCallback, addComment);
	}

	@Override
	protected IExtraReaderWriterConfigurator<LocalConfig> getLocalExtraReaderWriterConfigurator() {
		final IExtraReaderWriterConfigurator<LocalConfig> parent = super.getLocalExtraReaderWriterConfigurator();
		return new IExtraReaderWriterConfigurator<LocalConfig>() {
			@Override
			public void builder(IApiBuilder builder, LocalConfig apiConfig) {
				parent.builder(builder, apiConfig);
				builder.registerReader(ICommentsReader.class, commentsReader);
			}
		};
	}
	@Override
	protected void setUp() throws Exception {
		commentsReader = EasyMock.createMock(ICommentsReader.class);
		commentsCallback = EasyMock.createMock(ICommentsCallback.class);
		addComment = EasyMock.createMock(Runnable.class);
		super.setUp();
		cardConfig = IExplorerTest.addNeededResources(CardDataStoreFixture.syncCardConfig(display));
		comments = new Comments(shell, getLocalApi().makeReadWriter(), cardConfig, commentsCallback, addComment);
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(commentsReader, commentsCallback, addComment);
		super.tearDown();
		cardConfig.dispose();
	}

}