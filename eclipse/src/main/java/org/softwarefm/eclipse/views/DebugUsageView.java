package org.softwarefm.eclipse.views;

import java.io.UnsupportedEncodingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.SoftwareFmPlugin;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.shared.usage.IUsageListener;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.utilities.exceptions.Exceptions;
import org.softwarefm.utilities.strings.Strings;

public class DebugUsageView extends ViewPart {

	private StyledText text;
	private final IUsagePersistance persistance = IUsagePersistance.Utils.persistance();

	@Override
	public void createPartControl(Composite parent) {
		text = new StyledText(parent, SWT.WRAP | SWT.READ_ONLY);
		final IUsage usage = SoftwareFmPlugin.getDefault().getUsage();
		usage.addUsageListener(new IUsageListener() {
			@Override
			public void usageChanged() {
				try {
					String raw = persistance.saveUsageStats(usage.getStats());
					int rawBytes = raw.getBytes("UTF-8").length;
					int zippedBytes = Strings.zip(raw).length;
					text.setText("Raw: " + rawBytes + " Zipped: " + zippedBytes + "\n" + raw);
				} catch (UnsupportedEncodingException e) {
					text.setText(Exceptions.classAndMessage(e));
				}
			}
		});
	}

	@Override
	public void setFocus() {
	}

}