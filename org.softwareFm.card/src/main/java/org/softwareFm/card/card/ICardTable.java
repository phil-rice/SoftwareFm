package org.softwareFm.card.card;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.card.card.internal.CardTable;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasControl;

public interface ICardTable extends IHasControl {

	Table getTable();

	public static class Utils {
		public static ICardTable cardTable(Composite parent, CardConfig cardConfig, TitleSpec titleSpec, String cardType, Map<String, Object> data) {
			return new CardTable(parent, cardConfig, titleSpec, cardType, data);
		}
	}

}
