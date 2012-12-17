package org.softwarefm.softwarefm.views;

import java.io.UnsupportedEncodingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsageListener;
import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.softwarefm.SoftwareFmPlugin;
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
			public void usageOccured(String usageString) {
				try {
					String raw = persistance.save(usage);
					int rawBytes = raw.getBytes("UTF-8").length;
					int zippedBytes = Strings.zip(raw).length;
					text.setText(usageString + "\nRaw: " + rawBytes + " Zipped: " + zippedBytes + "\n" + raw);
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