package org.softwarefm.core.usage;

import java.util.ArrayList;
import java.util.Collections;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.softwarefm.core.client.AbstractMigComposite;
import org.softwarefm.core.client.IUserConnectionDetails;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.server.configurator.ConfiguratorConstants;
import org.softwarefm.shared.usage.IUsage;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageReporter;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.utilities.http.IResponse;

public class UsageComposite extends AbstractMigComposite {

	private final Text usageText;
	private final List localUsageList;
	private final IUsage usage;
	private final List remoteUsageList;
	private final IUsagePersistance usagePersistance;
	private final IUserConnectionDetails userConnectionDetails;

	public UsageComposite(Composite parent, final IUserConnectionDetails userConnectionDetails, final IUsage usage, final IUsagePersistance usagePersistance) {
		super(parent, new MigLayout("fill", "[][grow]", "[][][grow]"));
		this.userConnectionDetails = userConnectionDetails;
		this.usage = usage;
		this.usagePersistance = usagePersistance;
		Composite composite = getComposite();
		usageText = Swts.createMigLabelAndTextForForm(composite, "Path", "somePath");
		Swts.Buttons.makeMigPushButton(composite, "Send", "split 2, span 2").addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IUsageReporter reporter = IUsageReporter.Utils.reporter(userConnectionDetails.getHost(), userConnectionDetails.getPort());
				reporter.report(userConnectionDetails.getUser(), usage.getStats());
				updateLocalAndRemote();
			}
		});
		Swts.Buttons.makeMigPushButton(composite, "Refresh", "wrap").addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getRemoteUsage();

			}

		});
		localUsageList = Swts.createMigList(composite, SWT.NULL, "split 2, span 2, grow");
		remoteUsageList = Swts.createMigList(composite, SWT.NULL, "grow");
		usageText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				usage.selected(getUsage());
				updateDisplay(localUsageList, usage.getStats());
			}

		});
		updateLocalAndRemote();
	}

	private void updateLocalAndRemote() {
		updateDisplay(localUsageList, usage.getStats());
		getRemoteUsage();
	}

	private void updateDisplay(final List list, final IUsageStats stats) {
		getComposite().getDisplay().syncExec(new Runnable() {
			public void run() {
				list.removeAll();
				ArrayList<String> paths = new ArrayList<String>(stats.keys());
				Collections.sort(paths);
				for (String path : paths)
					list.add(path + " " + stats.get(path).count);
			}
		});
	}

	private void getRemoteUsage() {
		async(new Runnable() {
			public void run() {
				IResponse response =userConnectionDetails. getHttpClient().get(ConfiguratorConstants.userPattern, userConnectionDetails.getUser()).execute();
				IUsageStats usageStats = usagePersistance.parse(response.asString());
				updateDisplay(remoteUsageList, usageStats);
			}
		});
	}

	public String getUsage() {
		return usageText.getText();
	}

}
