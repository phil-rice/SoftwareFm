package org.softwareFm.card.internal.details;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.display.swt.Swts;

public interface IDetailsFactoryCallback extends ICardSelectedListener, IGotDataCallback, IAfterEditCallback {

public static class Utils{
	public static IDetailsFactoryCallback noCallback(){
		return new IDetailsFactoryCallback() {
			
			@Override
			public void afterEdit() {
			}
			
			@Override
			public void gotData(Control control) {
			}
			
			@Override
			public void cardSelected(ICard card) {
			}
		};
	}

	public static IDetailsFactoryCallback resizeAfterGotData() {
		return new IDetailsFactoryCallback(){
			@Override
			public void cardSelected(ICard card) {
			}

			@Override
			public void gotData(Control control) {
				Swts.setSizeFromClientArea(control);
			}

			@Override
			public void afterEdit() {
			}};
	}
}
}
