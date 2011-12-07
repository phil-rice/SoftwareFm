package org.softwareFm.card.editors;

import java.util.Map;

import org.softwareFm.card.card.ICardData;

public interface ICardEditorCallback{

	void ok(ICardData cardData);

	void cancel(ICardData cardData);

	boolean canOk(Map<String,Object> data);
}
