package org.softwareFm.card.details;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.details.internal.DetailFactory;
import org.softwareFm.display.composites.IHasControl;

/** creates a {@link IHasControl} to allow the user to view/edit a detail of the card*/
public interface IDetailFactory {

	IHasControl makeDetail(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback);

	public static class Utils {
		public static IDetailFactory detailsFactory(IDetailAdder... adders) {
			return new DetailFactory(Arrays.asList(adders));
		}
	}
}
