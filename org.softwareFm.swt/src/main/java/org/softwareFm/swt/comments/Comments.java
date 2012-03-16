/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.comments;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.client.http.constants.CommentConstants;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.comparators.Comparators;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.CommonMessages;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.comments.ICommentsReader;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeWithFooterLayout;
import org.softwareFm.swt.editors.IDataCompositeWithFooter;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;

public class Comments implements IHasControl {

	public static class CommentsButtons implements IHasComposite {
		private final Composite content;
		final Control addCommentButton;

		public CommentsButtons(Composite parent, CardConfig cardConfig, Runnable addButton) {
			this.content = new Composite(parent, SWT.NULL);
			String imageName = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, CollectionConstants.addCommentButtonImage);
			Image addCommentImage = Functions.call(cardConfig.imageFn, imageName);
			addCommentButton = Swts.Buttons.makeImageButton(content, addCommentImage, addButton);
//			addCommentButton = Swts.Buttons.makePushButton(content, "add", addButton);
			RowLayout layout = Swts.Row.getHorizonalNoMarginRowLayout();layout.marginRight=20;
			content.setLayout(layout);
			
		}

		@Override
		public Control getControl() {
			return content;
		}

		@Override
		public Composite getComposite() {
			return content;
		}

		public void setUserData(UserData userData) {
			addCommentButton.setEnabled(userData.softwareFmId != null);
		}
	}

	public static class CommentsComposite extends DataComposite<Table> implements IDataCompositeWithFooter<Table, CommentsButtons> {

		private final Table table;
		private final ICommentsReader commentsReader;
		protected List<Map<String, Object>> allComments;
		private String url;
		private final CommentsButtons footer;

		public CommentsComposite(Composite parent, CardConfig cardConfig, ICommentsReader commentsReader, final ICommentsCallback callback, Runnable addButton) {
			super(parent, cardConfig, CommentConstants.commentCardType, "");
			this.commentsReader = commentsReader;
			IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, CommentConstants.commentCardType);
			this.table = new Table(getInnerBody(), SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			table.setLinesVisible(true);
			new TableColumn(table, SWT.NONE).setText(IResourceGetter.Utils.getOrException(resourceGetter, CommentConstants.tableCreatorColumnTitle));
			new TableColumn(table, SWT.NONE).setText(IResourceGetter.Utils.getOrException(resourceGetter, CommentConstants.tableSourceColumnTitle));
			new TableColumn(table, SWT.NONE).setText(IResourceGetter.Utils.getOrException(resourceGetter, CommentConstants.tableTextColumnTitle));
			table.setHeaderVisible(true);
			table.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					int index = table.getSelectionIndex();
					if (index != -1 && index < allComments.size()) {
						Map<String, Object> comment = allComments.get(index);
						callback.selected(getCardType(), url, index, comment);
					}
				}
			});
			this.footer = new CommentsButtons(getInnerBody(), cardConfig, addButton);
		}

		@Override
		public CommentsButtons getFooter() {
			return footer;
		}

		@Override
		public Table getEditor() {
			return table;
		}

		public void populate(UserData userData, String cardType, String url) {
			this.url = url;
			CardConfig cc = getCardConfig();

			allComments = Lists.sort(ICommentsReader.Utils.allComments(commentsReader, url, userData.softwareFmId, userData.crypto, CommentConstants.globalSource, CommentConstants.mySource), //
					Comparators.invert(Comparators.mapKey(CommentConstants.timeKey)));
			String titleKey = allComments.size() > 0 ? CollectionConstants.commentsTitle : CollectionConstants.commentsNoTitle;

			String pattern = IResourceGetter.Utils.getOrException(cc.resourceGetterFn, cardType, titleKey);
			String titleText = MessageFormat.format(pattern, url, Strings.lastSegment(url, "/"), url);
			setTitleAndImage(titleText, "", cardType);
			footer.setUserData(userData);
			updateDisplay(allComments);
		}

		private void updateDisplay(List<Map<String, Object>> comments) {
			table.removeAll();
			int i = 0;
			for (Map<String, Object> comment : comments) {
				Object text = comment.get(CommentConstants.textKey);
				Object creator = comment.get(CommentConstants.creatorKey);
				Object source = comment.get(CommentConstants.sourceKey);
				if (comment.containsKey(CommonConstants.errorKey))
					text = CommonMessages.corrupted;
				TableItem item = new TableItem(table, SWT.NULL);
				item.setData(i++);
				item.setText(0, Strings.nullSafeToString(creator));
				item.setText(1, Strings.nullSafeToString(source));
				item.setText(2, Strings.nullSafeToString(text));
			}
			Swts.packColumns(table);
		}

	}

	private final CommentsComposite content;

	public Comments(Composite parent, CardConfig cardConfig, ICommentsReader commentsReader, ICommentsCallback callback, Runnable addComment) {
		content = new CommentsComposite(parent, cardConfig, commentsReader, callback, addComment);
		content.setLayout(Swts.titleAndContentLayout(cardConfig.titleHeight));
		content.setLayout(new DataCompositeWithFooterLayout());
	}

	public void showCommentsFor(UserData userData, String cardType, String url) {
		content.populate(userData, cardType, url);
	}

	@Override
	public Control getControl() {
		return content;
	}

	public void selectComment(String key) {
		Table table = content.table;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = content.table.getItem(i);
			if (key.equals(item.getData())) {
				table.select(i);
				table.showItem(item);
				table.notifyListeners(SWT.Selection, new Event());
				return;
			}
		}

	}

	public Table getTable() {
		return content.table;
	}

	public static void main(String[] args) {
		Swts.Show.display(Comments.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(from.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), new CardDataStoreMock()));
				Runnable addComment = Runnables.sysout("add comment");
				ICommentsReader commentsReader = ICommentsReader.Utils.mockReader("sfmId", "Phil", System.currentTimeMillis(), "comment1", "comment2", "comment3");
				Comments comments = new Comments(from, cardConfig, commentsReader, new ICommentsCallback() {

					@Override
					public void selected(String cardType, String url, int index, Map<String, Object> comment) {
						System.out.println("Selected: " + cardType + ", " + url + ", " + index + ", " + comment);
					}
				}, addComment);
				comments.showCommentsFor(new UserData("email", "someSfmId", "someCrypto"), "artifact", "someUrl");
				return (Composite) comments.getControl();
			}
		});
	}
}