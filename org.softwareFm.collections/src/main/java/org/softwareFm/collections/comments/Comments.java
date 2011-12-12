package org.softwareFm.collections.comments;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.composites.CompositeWithCardMargin;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class Comments implements IHasControl {

	static class CommentsComposite extends CompositeWithCardMargin {

		private final Map<String, Object> map = Maps.newMap();
		private final Table table;
		private final TableColumn titleColumn;
		private final TableColumn textColumn;
		private final CardConfig cc;
		private final Composite body;
		private final Label label;

		public CommentsComposite(Composite parent, CardConfig cardConfig, final ICommentsCallback callback) {
			super(parent, SWT.NULL, cardConfig);
			this.cc = cardConfig;
			this.body = new Composite(this, SWT.BORDER);
			body.setLayout(Swts.titleAndContentLayout(cc.titleHeight));
			label = new Label(body, SWT.NULL);
			label.setText("Comments");
			this.table = new Table(body, SWT.V_SCROLL | SWT.FULL_SELECTION);
			table.setLinesVisible(true);
			this.titleColumn = new TableColumn(table, SWT.NONE);
			this.textColumn = new TableColumn(table, SWT.NONE);
			table.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					int index = table.getSelectionIndex();
					if (index != -1) {
						Object object = map.get(table.getItem(index).getData());
						if (object instanceof Map<?, ?>) {
							Map<String, Object> entryMap = (Map<String, Object>) object;
							Object title = entryMap.get("title");
							Object text = entryMap.get("text");
							callback.selected(title, text);
						}
					}
				}
			});
		}

		public void setInitialComments(String title, Map<String, Object> map) {
			this.map.clear();
			this.map.putAll(map);
			label.setText(title);
			updateDisplay();
		}

		public void setColorsFor(ICardData cardData){
			CardConfig cardConfig = cardData.getCardConfig();
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, cardData);
			table.setBackground(titleSpec.background);
			label.setBackground(titleSpec.titleColor);
			
		}
		private void updateDisplay() {
			table.removeAll();
			for (Entry<String, Object> entry : map.entrySet()) {
				if (entry.getValue() instanceof Map<?, ?>) {
					Map<String, Object> entryMap = (Map<String, Object>) entry.getValue();
					Object title = entryMap.get("title");
					Object text = entryMap.get("text");
					if (title != null) {
						TableItem item = new TableItem(table, SWT.NULL);
						item.setData(entry.getKey());
						item.setText(0, title.toString());
						item.setText(1, text.toString());
					}
				}
			}
			titleColumn.pack();
			textColumn.pack();
		}
	}

	private final CommentsComposite content;

	public Comments(Composite parent, CardConfig cardConfig, ICommentsCallback callback) {
		content = new CommentsComposite(parent, cardConfig, callback);
		content.setLayout(new FillLayout());
	}

	public void showCommentsFor(ICardData cardData) {
		CardConfig cardConfig = cardData.getCardConfig();
		Object commentObject = cardData.data().get(CollectionConstants.comment);
		List<String> comments = Lists.newList();
		if (commentObject instanceof Map<?, ?>)
			setComment(cardData, cardConfig, CollectionConstants.commentsTitle, (Map<String, Object>) commentObject);
		else
			setComment(cardData, cardConfig, CollectionConstants.commentsNoTitle, Maps.stringObjectMap());
	}

	private void setComment(ICardData cardData, CardConfig cardConfig, String titleKey, Map<String, Object> map) {
		String pattern = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardData.cardType(), titleKey);
		String title = MessageFormat.format(pattern, cardData.url(), Strings.lastSegment(cardData.url(), "/"), cardData.cardType());
		content.setInitialComments(title, map);
		content.setColorsFor(cardData);
	}

	@Override
	public Control getControl() {
		return content;
	}
}
