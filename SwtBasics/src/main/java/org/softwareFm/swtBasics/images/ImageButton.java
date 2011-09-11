package org.softwareFm.swtBasics.images;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class ImageButton implements IHasControl {

	public static class Utils {
		public static void setEnabledIfNotBlank(ImageButton button, String value) {
			if (Strings.nullSafeToString(value).equals(""))
				button.disableButton(SwtBasicConstants.noValueSet);
			else
				button.enableButton();
		}
	}

	private final Label label;
	private boolean state;
	private final List<IImageButtonListener> listeners = Lists.newList();
	private boolean enabled = true;
	private String tooltipText;
	private String reasonForDisable;
	private final String overlayKey;
	private final Map<SmallIconPosition, String> smallIconMap = Maps.newMap();
	private final ImageRegistry imageRegistry;
	private String mainImageKey;
	private final Composite content;

	public ImageButton(Composite parent, ImageRegistry imageRegistry, String key, final boolean toggle) {
		this(parent, imageRegistry, key, null, toggle);
	}

	public ImageButton(Composite parent, final ImageRegistry imageRegistry, final String key, String overlayKey, final boolean toggle) {
		this.content = new Composite(parent, SWT.NULL);
		this.imageRegistry = imageRegistry;
		this.overlayKey = overlayKey;
		content.setLayout(Swts.getGridLayoutWithoutMargins());
		this.label = new Label(content, SWT.NULL);
		Image mainBackdrop = imageRegistry.get("backdrop.main");
		if (mainBackdrop == null)
			throw new NullPointerException();
		label.setImage(mainBackdrop);
		this.mainImageKey = key;
		label.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				System.out.println("Painting button: " + mainImageKey + ", " + ImageButton.this.overlayKey + ", " + smallIconMap);
				if (state) {
					Image depressed = getImage("backdrop.depressed");
					e.gc.drawImage(depressed, 0, 0);
				}
				Image mainImage = getImage(mainImageKey);
				e.gc.drawImage(mainImage, 2, 2);
				if (ImageButton.this.overlayKey != null) {
					Image overLayImage = imageRegistry.get(ImageButton.this.overlayKey);
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
		});
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (enabled) {
					if (toggle)
						setState(!state);
					for (IImageButtonListener listener : listeners)
						try {
							listener.buttonPressed(ImageButton.this);
						} catch (Exception e1) {
							throw WrappedException.wrap(e1);
						}
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		GridData data = Swts.makeGrabHorizonalAndFillGridData();
		data.widthHint = 20;
		data.heightHint = 20;
		label.setLayoutData(data);
	}

	private Image getImage(String key) {
		Image result = imageRegistry.get(key);
		if (result == null)
			throw new NullPointerException(MessageFormat.format(SwtBasicConstants.cannotFindImage, key));
		return result;
	}

	public void clearSmallIcons() {
		smallIconMap.clear();
		label.redraw();
	}

	public void setSmallIcon(SmallIconPosition position, String key) {
		smallIconMap.put(position, key);
		label.redraw();
	}

	public void setTooltipText(String tooltipText) {
		this.tooltipText = tooltipText;
		updateTooltipText();
	}

	private void updateTooltipText() {
		StringBuilder builder = new StringBuilder();
		builder.append(Strings.nullSafeToString(tooltipText));
		if (reasonForDisable != null) {
			if (builder.length() > 0)
				builder.append("\n");
			builder.append(reasonForDisable);
		}
		label.setToolTipText(builder.toString());
	}

	public void addListener(IImageButtonListener listener) {
		listeners.add(listener);
	}

	public void enableButton() {
		this.enabled = true;
		this.reasonForDisable = null;
		updateTooltipText();

	}

	public void disableButton(String reason) {
		this.reasonForDisable = reason;
		this.enabled = false;
		updateTooltipText();

	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
		label.redraw();
		label.update();
	}

	public Image getImage() {
		return label.getImage();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

	}

	public void setImage(String key) {
		this.mainImageKey = key;
		label.redraw();
	}

	@Override
	public String toString() {
		return "ImageButton: {" + mainImageKey + ", " + overlayKey + ", " + smallIconMap + "}";
	}

	public static void main(String[] args) {
		System.out.println("See ImageButtonDemo");
	}

	@Override
	public Control getControl() {
		return content;
	}

	public void setLayoutData(RowData rowData) {
		content.setLayoutData(rowData);
	}
}
