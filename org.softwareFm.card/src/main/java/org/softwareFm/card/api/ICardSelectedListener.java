package org.softwareFm.card.api;

import org.eclipse.swt.events.MouseEvent;

public interface ICardSelectedListener {

	void cardSelectedDown(ICard card, MouseEvent e);

	void cardSelectedUp(ICard card, MouseEvent e);

}
