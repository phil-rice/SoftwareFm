package org.softwarefm.labelAndText;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;

public abstract class TextAndFormComposite extends TextAndControlComposite<Form> {

	private Form form;

	public TextAndFormComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
	}

	@Override
	protected Form makeComponent(SoftwareFmContainer<?> container, Composite parent) {
		return form = new Form(parent, SWT.NULL, container, makeButtonConfigurator(), makeKeys());
	}

	public void setText(String key, String text) {
		form.setText(key, text);
	}

	public void setEnabledForButton(String key, boolean enabled) {
		form.setEnabledForButton(key, enabled);
	}
	
	public String getText(String key){
		if (form == null)
			return "";
		return form.getText(key);
	}

	abstract protected IButtonConfigurator makeButtonConfigurator();

	abstract protected String[] makeKeys();

}
