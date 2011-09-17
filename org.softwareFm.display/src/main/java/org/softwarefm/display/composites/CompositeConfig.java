package org.softwarefm.display.composites;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.softwareFm.softwareFmImages.backdrop.BackdropAnchor;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.display.SoftwareFmLayout;
import org.softwarefm.display.smallButtons.ImageButtonConfig;

public class CompositeConfig {
	public final SoftwareFmLayout layout;
	public final ImageRegistry imageRegistry;
	public final IResourceGetter resourceGetter;
	public final ImageButtonConfig imageButtonConfig;
	public Color notEditingBackground;
	public Color editingBackground;

	public CompositeConfig(Device device, SoftwareFmLayout layout, ImageRegistry imageRegistry, IResourceGetter resourceGetter) {
		super();
		this.layout = layout;
		this.imageRegistry = imageRegistry;
		this.resourceGetter = resourceGetter;
		this.imageButtonConfig = new ImageButtonConfig(layout, imageRegistry, BackdropAnchor.depressed, BackdropAnchor.main, null, null);
		this.notEditingBackground = device.getSystemColor(SWT.COLOR_GRAY);
		this.editingBackground = device.getSystemColor(SWT.COLOR_WHITE);
	}

}
