package org.softwareFm.card.internal.details;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.IDetailFactory;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasControl;

public class DetailFactory implements IDetailFactory {

	private final List<IDetailAdder> detailAdders;

	public DetailFactory(List<IDetailAdder> detailAdders) {
		this.detailAdders = detailAdders;
	}

	@Override
	public IHasControl makeDetail(Composite parentComposite, ICard parentCard, CardConfig cardConfig, KeyValue keyValue, ICardSelectedListener listener, Runnable afterEdit) {
		if (keyValue == null)
			return null;
		for (IDetailAdder adder : detailAdders) {
			IHasControl result = adder.add(parentComposite, parentCard, cardConfig, keyValue, listener, afterEdit);
			if (result != null)
				return result;
		}
		return null;
	}

}
