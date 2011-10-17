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
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.IDetailFactory;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;

public class DetailFactory implements IDetailFactory {

	@SuppressWarnings("unchecked")
	@Override
	public Composite makeDetail(Composite parent, final int style, final ICardFactory cardFactory, final ICardSelectedListener listener, final String url, KeyValue keyValue) {
		Object value = keyValue.value;
		if (value instanceof String) {
			Composite composite = new Composite(parent, SWT.NULL);
			Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
			return composite;

		} else if (value instanceof List) {
			List<Object> list = (List<Object>) value;
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new FillWithAspectRatioLayoutManager(5, 3));
			List<KeyValue> keyValues = Lists.newList();
			for (Object object : list)
				if (object instanceof KeyValue)
					keyValues.add((KeyValue) object);
			Collections.sort(keyValues, cardFactory.comparator());
			for (final KeyValue childKeyValue : keyValues) {
				final CardHolder holder = new CardHolder(composite, childKeyValue.key);
				holder.getControl().addPaintListener(new PaintListener() {
					private boolean reported;

					@Override
					public void paintControl(PaintEvent e) {
						if (reported)
							return;
						cardFactory.makeCard(holder.getComposite(), style, false, url +"/"+ childKeyValue.key, new ICallback<ICard>(){
							@Override
							public void process(final ICard card) throws Exception {
								card.getControl().addMouseListener(new MouseAdapter() {
									@Override
									public void mouseDown(MouseEvent e) {
										listener.mouseDown(card, e);
									}
								});
								holder.setCard(card);
								
							}});
						reported = true;
					}
				});
			}
			return composite;
		} else if (value instanceof Map) {
			return makeDetail(parent, style, cardFactory, listener, url, new KeyValue(keyValue.key, Arrays.asList(keyValue)));
		} else
			return makeDetail(parent, style, cardFactory, listener, url, new KeyValue(keyValue.key, Strings.nullSafeToString(keyValue.value)));
	}
}
