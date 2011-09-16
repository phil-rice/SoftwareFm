package org.softwarefm.display.smallButtons;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swtBasics.IControlWithToggle;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.utilities.maps.Maps;
import org.softwarefm.display.data.DisplayConstants;

public class SimpleImageButton implements IHasControl, IControlWithToggle {

	private final Canvas content;
	private final Map<SmallIconPosition, String> smallIconMap = Maps.newMap();
	private boolean value;
	private final ImageButtonConfig config;

	public SimpleImageButton(Composite parent, final ImageButtonConfig config) {
		this.config = config;
		this.content = new Canvas(parent, SWT.NULL){
			@Override
			public String toString() {
				return getClass().getSimpleName() +"[main=" + config.mainImage + ",overlay=" + config.overlayImage +"]"+super.toString();
		}
		};
		config.validate();
		content.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				Image background = getImage(value ? config.depressedBackground : config.normalBackground);
				e.gc.drawImage(background, 0, 0);

				Image mainImage = getImage(config.mainImage);
				e.gc.drawImage(mainImage, 2, 2);
				if (config.overlayImage != null) {
					Image overLayImage = getImage(config.overlayImage);
					e.gc.drawImage(overLayImage, 2, 2);
				}
				for (SmallIconPosition pos : SmallIconPosition.values()) {
					String key = smallIconMap.get(pos);
					if (key != null) {
						Image image = getImage(key);
						e.gc.drawImage(image, pos.x + 2, pos.y + 2);
					}
				}
			}

			private Image getImage(String string) {
				Image result = config.imageRegistry.get(string);
				if (result == null)
					throw new IllegalStateException(MessageFormat.format(DisplayConstants.imageNotFound, string));
				return result;
			}
		});
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public boolean value() {
		return value;
	}

	@Override
	public void setValue(boolean value) {
		this.value = value;
		content.layout();
		content.redraw();
	}

	public void addListener(IImageButtonListener imageButtonListener) {
		
	}

}
