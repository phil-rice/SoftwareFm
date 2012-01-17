package org.softwareFm.collections.mySoftwareFm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.composites.TextInBorder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;

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

		public static IShowMessage textInBorder(final Composite parent, final CardConfig cardConfig) {
			return new IShowMessage() {
				@Override
				public void showMessage(String cardType, String title, String message) {
					Swts.removeAllChildren(parent);
					Functions.call(TextInBorder.makeTextFromString(SWT.READ_ONLY | SWT.WRAP, cardConfig, cardType, title, message), parent);
					parent.layout();
				}
			};
		}

	}

}
