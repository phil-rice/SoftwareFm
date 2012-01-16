package org.softwareFm.collections.mySoftwareFm;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.mail.IMail;

public class MySoftwareFm implements IHasControl {

	public final static String email = "email";
	public final static String cryptoKey = "cryptoKey";
	
	public static class MySoftwareFmComposite extends Composite {
		private final Map<String, Object> myDetails;

		public MySoftwareFmComposite(Composite parent, Map<String, Object> initialDetails) {
			super(parent, SWT.NULL);
			this.myDetails = initialDetails;
		}

	}

	private final MySoftwareFmComposite content;

	public MySoftwareFm(Composite parent, IMail mail, Map<String,Object> initialDetails) {
		this.content = new MySoftwareFmComposite(parent, initialDetails);
	}

	@Override
	public Control getControl() {
		return content;
	}

}
