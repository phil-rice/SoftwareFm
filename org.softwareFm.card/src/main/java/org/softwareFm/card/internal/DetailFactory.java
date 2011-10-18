package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.IDetailFactory;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class DetailFactory implements IDetailFactory {

	@SuppressWarnings("unchecked")
	@Override
	public Composite makeDetail(Composite parentComposite, final ICard parentCard, final CardConfig cardConfig, KeyValue keyValue, final ICardSelectedListener listener) {
		Object value = keyValue.value;
		if (value instanceof String) {
			Composite composite = new Composite(parentComposite, SWT.NULL);
			Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
			return composite;

		} else if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			Composite composite = new Composite(parentComposite, SWT.NULL);
			composite.setLayout(new FillWithAspectRatioLayoutManager(5, 3));
			List<KeyValue> keyValues = Lists.newList();
			for (Object object : list)
				if (object instanceof KeyValue)
					keyValues.add((KeyValue) object);
			Collections.sort(keyValues, cardConfig.cardFactory.comparator());
			int i = 0;
			for (final KeyValue childKeyValue : keyValues) {
				final CardHolder holder = new CardHolder(composite, IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "detail.factory.loading.text"), childKeyValue.key + "/" + i++);
				holder.getControl().addPaintListener(new PaintListener() {

					@Override
					public void paintControl(PaintEvent e) {
						holder.getControl().removePaintListener(this);
						ICardFactory.Utils.makeCard(holder.getComposite(), cardConfig.withStyleAndSelection(SWT.NO_SCROLL | SWT.FULL_SELECTION, false), parentCard.url() + "/" + childKeyValue.key, new ICallback<ICard>() {
							@Override
							public void process(final ICard card) throws Exception {
								if (card == null)
									return;
								card.getControl().addMouseListener(new MouseAdapter() {
									@Override
									public void mouseDown(MouseEvent e) {
										listener.cardSelectedDown(card, e);
									}

									@Override
									public void mouseUp(MouseEvent e) {
										listener.cardSelectedUp(card, e);
									}
								});
								holder.setCard(card);

							}
						});
					}
				});
			}
			return composite;
		} else if (value instanceof Map) {
			return makeDetail(parentComposite, parentCard, cardConfig, new KeyValue(keyValue.key, Arrays.asList(keyValue)), listener);
		} else
			return makeDetail(parentComposite, parentCard, cardConfig, new KeyValue(keyValue.key, Strings.nullSafeToString(keyValue.value)), listener);
	}
}
