package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.swt.card.composites.TextInBorderWithClick;
import org.softwareFm.swt.configuration.CardConfig;

public interface IShowMessage {

	void showMessage(String cardType, String title, String message);

	public static class Utils {

		public static IShowMessage sysout() {
			return new IShowMessage() {
				@Override
				public void showMessage(String cardType, String title, String message) {
					System.out.println(title + " " + message);
				}
			};
		}

		public static IShowMessage textInBorder(final Composite parent, final CardConfig cardConfig, final Runnable onClick) {
			return new IShowMessage() {
				@Override
				public void showMessage(String cardType, String title, String message) {
					Swts.removeAllChildren(parent);
					Functions.call(TextInBorderWithClick.makeTextFromString(SWT.WRAP|SWT.READ_ONLY, cardConfig, cardType, title, message, onClick), parent);
					parent.layout();
				}
			};
		}

	}

}
