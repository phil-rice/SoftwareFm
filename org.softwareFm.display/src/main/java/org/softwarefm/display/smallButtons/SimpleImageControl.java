package org.softwareFm.display.smallButtons;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.simpleButtons.SmallIconPosition;
import org.softwareFm.utilities.maps.Maps;

public class SimpleImageControl extends Canvas {

	private boolean value;
	private Map<SmallIconPosition, String> smallIconMap = Maps.newMap();
	public final ImageButtonConfig config;

	public SimpleImageControl(Composite parent, int style, final ImageButtonConfig config, final boolean showBackground) {
		super(parent, style);
		this.config = config;
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				if (showBackground) {
					Image background = getImage(value ? config.depressedBackground : config.normalBackground);
					e.gc.drawImage(background, 0, 0);
				}
				Image mainImage = getImage(config.mainImage, isEnabled());
				e.gc.drawImage(mainImage, 2, 2);
				if (config.overlayImage != null) {
					Image overLayImage = getImage(config.overlayImage, isEnabled());
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
				return getImage(string, true);
			}

			private Image getImage(String string, boolean enabled) {
				String fullName = enabled ? string : string + ".inactive";
				Image result = config.imageRegistry.get(fullName);
				if (result == null)
					throw new IllegalStateException(MessageFormat.format(DisplayConstants.imageNotFound, fullName));
				return result;
			}
		});
	}

	public void setSmallIconMap(Map<SmallIconPosition, String> smallIconMap) {
		this.smallIconMap = smallIconMap;
		layout();
		redraw();
	}

	public boolean value() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
		layout();
		redraw();
	}

	@Override
	public void setToolTipText(String string) {
		super.setToolTipText(string);
	}

	public void setSmallIcon(SmallIconPosition key, String string) {
		this.smallIconMap.put(key, string);
		layout();
		redraw();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (enabled != isEnabled())
			redraw();
		super.setEnabled(enabled);
	}
}
