package org.softwareFm.collections.explorer.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.editors.ICardEditorCallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class AddCardCallbackMock implements ICardEditorCallback {

	private final List<Map<String, Object>> okData = Lists.newList();
	private final List<Map<String, Object>> cancelData = Lists.newList();
	private final List<Map<String, Object>> canOkData = Lists.newList();

	private boolean canOk;

	@Override
	public void ok(ICardData cardData) {
		okData.add(Maps.copyMap(cardData.data()));
	}

	@Override
	public void cancel(ICardData cardData) {
		cancelData.add(Maps.copyMap(cardData.data()));
	}

	public void setCanOk(boolean canOk) {
		this.canOk = canOk;
	}

	@Override
	public boolean canOk(Map<String, Object> data) {
		canOkData.add(Maps.copyMap(data));
		return canOk;
	}

}
