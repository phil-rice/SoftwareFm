package org.softwarefm.labelAndText;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.constants.SwtErrorMessages;
import org.softwarefm.eclipse.swt.HasComposite;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.collections.Lists;

public class ButtonComposite extends HasComposite {

	public static class ButtonCompositeLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			return Swts.computeSizeForHorizontallyStackedComposites(wHint, hHint, composite.getChildren());
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			Rectangle ca = composite.getClientArea();
			Point idealSize = computeSize(composite, SWT.DEFAULT, SWT.DEFAULT, false);
			int x = Math.max(SwtConstants.xIndent, ca.width - idealSize.x - SwtConstants.xIndent);
			for (Control control : composite.getChildren()) {
				int width = control.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
				control.setBounds(x, ca.y, width, ca.height);
				x += width;
			}
		}

	}

	public ButtonComposite(Composite parent) {
		super(parent);
		setLayout(new ButtonCompositeLayout());
	}

	public void setEnabledForButton(String key, boolean enabled) {
		getButton(key).setEnabled(enabled);
	}

	public Control getButton(String key) {
		List<Object> keys = Lists.newList();
		for (Control child : getComposite().getChildren()) {
			IButtonConfig data = (IButtonConfig) child.getData();
			if (data == null)
				throw new IllegalStateException();
			if (key.equals(data.key())) {
				return child;
			} else
				keys.add(data);
		}
		throw new IllegalArgumentException(MessageFormat.format(SwtErrorMessages.unrecognisedKey, key, keys));
	}

	public void updateButtonStatus(IGetTextWithKey getTextWithKey) {
		for (Control child : getComposite().getChildren()) {
			IButtonConfig buttonConfig = (IButtonConfig) child.getData();
			if (buttonConfig == null)
				throw new IllegalStateException();
			boolean canExecute = buttonConfig.canExecute(getTextWithKey);
			child.setEnabled(canExecute);
		}
	}

}
