package org.softwareFm.collections.comments;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class Comments implements IHasControl {

	static class CommentsComposite extends Composite {

		private final Map<String, Object> map = Maps.newMap();
		private final Table table;
		private final TableColumn titleColumn;
		private final TableColumn textColumn;

		public CommentsComposite(Composite parent) {
			super(parent, SWT.NULL);
			this.table = new Table(this, SWT.V_SCROLL);
			this.titleColumn = new TableColumn(table, SWT.NONE);
			this.textColumn = new TableColumn(table, SWT.NONE);
		}

		public void setInitialComments(Map<String, Object> map) {
			this.map.clear();
			this.map.putAll(map);
			updateDisplay();
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

	public Comments(Composite parent) {
		content = new CommentsComposite(parent);
		content.setLayout(new FillLayout());
	}

	public void showCommentsFor(ICardData cardData) {
		CardConfig cardConfig = cardData.getCardConfig();
		Object commentObject = cardData.data().get(CollectionConstants.comment);
		List<String> comments = Lists.newList();
		if (commentObject instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) commentObject;
			content.setInitialComments(map);
		}
	}

	@Override
	public Control getControl() {
		return content;
	}
}
