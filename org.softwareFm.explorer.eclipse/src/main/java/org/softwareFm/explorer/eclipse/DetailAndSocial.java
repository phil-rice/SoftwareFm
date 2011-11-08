package org.softwareFm.explorer.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasComposite;

public class DetailAndSocial implements IHasComposite {

	static class DetailAndSocialComposite extends SashForm {

		private final Composite details;
		private final Composite social;

		public DetailAndSocialComposite(Composite parent, int style) {
			super(parent, style);
			details = new Composite (this, SWT.NULL);
			social = new Composite (this, SWT.NULL);
			setWeights(new int[] { 1, 1 });
		}
	}

	private final DetailAndSocialComposite content;

	public DetailAndSocial(Composite parent, int style) {
		content = new DetailAndSocialComposite(parent, style);
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public Control getControl() {
		return content;
	}

}
