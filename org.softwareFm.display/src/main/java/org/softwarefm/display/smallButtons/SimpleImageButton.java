package org.softwareFm.display.smallButtons;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.ISmallDisplayer;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.exceptions.WrappedException;

public class SimpleImageButton implements IHasControl, ISmallDisplayer {

	private final SimpleImageControl content;

	public SimpleImageButton(IButtonParent parent, final ImageButtonConfig config) {
		config.validate();
		this.content = new SimpleImageControl(parent.getButtonComposite(), SWT.NULL, config);
		parent.buttonAdded(this);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public boolean value() {
		return content.value();
	}

	@Override
	public void setValue(boolean value) {
		content.setValue(value);
		content.redraw();
		content.layout();
	}

	public void addListener(final IImageButtonListener imageButtonListener) {
		content.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					imageButtonListener.buttonPressed(SimpleImageButton.this);
				} catch (Exception e1) {
					throw WrappedException.wrap(e1);
				}
			}
		});

	}

	public void setSmallIconMap(Map<SmallIconPosition, String> smallIconMap) {
		content.setSmallIconMap(smallIconMap);
	}

	public void setSmallIcon(SmallIconPosition key, String string) {
		content.setSmallIcon(key, string);

	}

	@Override
	public void data(IDataGetter dataGetter, String entity, String url) {
	}

}
