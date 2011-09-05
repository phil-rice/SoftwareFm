package org.softwareFm.swtBasics.images;

import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
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
	private final ImageRegistry imageRegistry;
	private final String key;

	public ImageButton(Composite parent, ImageRegistry imageRegistry, String key, final boolean toggle) {
		this.imageRegistry = imageRegistry;
		this.key = key;
		this.label = new Label(parent, SWT.NULL);
		updateImage();
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (enabled) {
					if (toggle)
						state = !state;
					updateImage();
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

	private void updateImage() {
		try {
			if (state)
				label.setImage(Images.getMainImage(imageRegistry, key));
			else
				label.setImage(Images.getDepressedImage(imageRegistry, key));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
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
		updateImage();
	}

	@Override
	public Control getControl() {
		return label;
	}

	public Image getImage() {
		return label.getImage();
	}

	public static void main(String[] args) {
		Swts.display("Label", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(new FormLayout());
				ImageRegistry imageRegistry = new ImageRegistry();
				Images.registerImages(from.getDisplay(), imageRegistry, Images.class, SwtBasicConstants.key);
				ImageButton btn = new ImageButton(composite, imageRegistry, SwtBasicConstants.key, true);
				btn.addListener(new IImageButtonListener() {
					@Override
					public void buttonPressed(ImageButton button) {
						System.out.println(button.getState());
					}
				});
				FormData fd = new FormData();
				fd.bottom = new FormAttachment(100, 0);
				fd.right = new FormAttachment(100, 0);
				fd.top = new FormAttachment(0, 0);
				fd.left = new FormAttachment(0, 0);
				btn.getControl().setLayoutData(fd);
				return composite;
			}
		});
	}

	public void setLayoutData(RowData data) {
		this.label.setLayoutData(data);

	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

	}
}
