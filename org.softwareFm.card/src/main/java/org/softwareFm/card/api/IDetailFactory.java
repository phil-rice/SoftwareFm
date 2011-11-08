package org.softwareFm.card.api;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.internal.details.DetailFactory;
import org.softwareFm.card.internal.details.IDetailAdder;
import org.softwareFm.display.composites.IHasControl;

public interface IDetailFactory {

	IHasControl makeDetail(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback);

	public static class Utils {
		public static IDetailFactory detailsFactory(IDetailAdder... adders) {
			return new DetailFactory(Arrays.asList(adders));
		}
	}
}
