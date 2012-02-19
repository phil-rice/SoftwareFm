/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.comments;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.comments.ICommentsReader;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.composites.CompositeWithCardMargin;
import org.softwareFm.swt.card.composites.CompositeWithEditorIndent;
import org.softwareFm.swt.card.composites.OutlinePaintListener;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.title.Title;
import org.softwareFm.swt.title.TitleSpec;

public class Comments implements IHasControl {

	static class CommentsComposite extends CompositeWithCardMargin {

		private final Table table;
		private final TableColumn titleColumn;
		private final TableColumn textColumn;
		private final CardConfig cc;
		private final Composite body;
		private final Title title;
		private final ICommentsReader commentsReader;
		private final TableColumn sourceColumn;
		private final Label addCommentButton;

		public CommentsComposite(Composite parent, CardConfig cardConfig, ICommentsReader commentsReader, final ICommentsCallback callback, Runnable addButton) {
			super(parent, SWT.NULL, cardConfig);
			this.cc = cardConfig;
			this.commentsReader = commentsReader;
			Composite header = Swts.newComposite(this, SWT.NULL, "header");
			header.setLayout(Swts.titleAndRhsLayout(cardConfig.editorIndentX));
			title = new Title(header, cardConfig, TitleSpec.noTitleSpec(getBackground()), "Comments", "");
			String imageName = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, CollectionConstants.addCommentButtonImage);
			Image addCommentImage = Functions.call(cardConfig.imageFn, imageName);
			addCommentButton = Swts.Buttons.makeImageButton(header, addCommentImage, addButton);
			IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, CommentConstants.commentCardType);
			this.body = new CompositeWithEditorIndent(this, SWT.NULL, cardConfig);
			this.addPaintListener(new OutlinePaintListener(cardConfig));
			body.setLayout(new FillLayout());
			this.table = new Table(body, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			table.setLinesVisible(true);
			(this.titleColumn = new TableColumn(table, SWT.NONE)).setText(IResourceGetter.Utils.getOrException(resourceGetter, CommentConstants.tableCreatorColumnTitle));
			(this.sourceColumn = new TableColumn(table, SWT.NONE)).setText(IResourceGetter.Utils.getOrException(resourceGetter, CommentConstants.tableSourceColumnTitle));
			(this.textColumn = new TableColumn(table, SWT.NONE)).setText(IResourceGetter.Utils.getOrException(resourceGetter, CommentConstants.tableTextColumnTitle));
			table.setHeaderVisible(true);
			table.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					int index = table.getSelectionIndex();
					if (index != -1) {
						// Object object = map.get(table.getItem(index).getData());
						// if (object instanceof Map<?, ?>) {
						// Map<String, Object> entryMap = (Map<String, Object>) object;
						// String title = Strings.nullSafeToString(entryMap.get("title"));
						// String text = Strings.nullSafeToString(entryMap.get("text"));
						// callback.selected(cardType, title, text);
						// }
					}
				}
			});
		}

		public void populate(UserData userData, String cardType, String url) {
			List<Map<String, Object>> comments = ICommentsReader.Utils.allComments(commentsReader, url, userData.softwareFmId, userData.crypto, CommentConstants.globalSource, CommentConstants.mySource);
			String titleKey = comments.size() > 0 ? CollectionConstants.commentsTitle : CollectionConstants.commentsNoTitle;
			TitleSpec titleSpec = Functions.call(cc.titleSpecFn, ICardData.Utils.create(cc, cardType, url, Maps.emptyStringObjectMap()));

			String pattern = IResourceGetter.Utils.getOrException(cc.resourceGetterFn, cardType, titleKey);
			String titleText = MessageFormat.format(pattern, url, Strings.lastSegment(url, "/"), url);
			title.setTitleAndImage(titleText, "", titleSpec);
			body.setBackground(titleSpec.titleColor);
			addCommentButton.setEnabled(userData.softwareFmId != null);
			updateDisplay(comments);
		}

		private void updateDisplay(List<Map<String, Object>> comments) {
			table.removeAll();
			int i = 0;
			for (Map<String, Object> comment : comments) {
				Object text = comment.get(CommentConstants.textKey);
				Object creator = comment.get(CommentConstants.creatorKey);
				Object source = comment.get(CommentConstants.sourceKey);
				if (title != null) {
					TableItem item = new TableItem(table, SWT.NULL);
					item.setData(i++);
					item.setText(0, Strings.nullSafeToString(creator));
					item.setText(1, Strings.nullSafeToString(source));
					item.setText(2, Strings.nullSafeToString(text));
				}
			}
			Swts.packColumns(table);
		}

	}

	private final CommentsComposite content;
	private final ICommentsReader commentsReader;
	private final CardConfig cardConfig;

	public Comments(Composite parent, CardConfig cardConfig, ICommentsReader commentsReader, ICommentsCallback callback, Runnable addComment) {
		this.cardConfig = cardConfig;
		this.commentsReader = commentsReader;
		content = new CommentsComposite(parent, cardConfig, commentsReader, callback, addComment);
		content.setLayout(Swts.titleAndContentLayout(cardConfig.titleHeight));
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

	public Title getTitle() {
		return content.title;
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
					public void selected(String cardType, String title, String text) {
						System.out.println("Selected: " + title + "," + text);
					}
				}, addComment);
				comments.showCommentsFor(new UserData("email", "someSfmId", "someCrypto"), "artifact", "someUrl");
				return (Composite) comments.getControl();
			}
		});
	}
}