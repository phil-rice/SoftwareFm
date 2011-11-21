package org.softwareFm.card.details.internal;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailFactory;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasControl;

public class DetailFactory implements IDetailFactory {

	private final List<IDetailAdder> detailAdders;

	public DetailFactory(List<IDetailAdder> detailAdders) {
		this.detailAdders = detailAdders;
	}

	@Override
	public IHasControl makeDetail(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (key == null)
			return null;
		for (IDetailAdder adder : detailAdders) {
			IHasControl result = adder.add(parentComposite, parentCard, cardConfig, key, value, callback);
			if (result != null)
				return result;
		}
		return null;
	}

}
