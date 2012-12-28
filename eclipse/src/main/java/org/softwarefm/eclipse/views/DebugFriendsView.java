package org.softwarefm.eclipse.views;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.core.url.HostOffsetAndUrl;
import org.softwarefm.eclipse.SoftwareFmPlugin;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;

public class DebugFriendsView extends ViewPart {

	private final IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
	private DebugFriendsComposite composite;

	static class DebugFriendsComposite extends Composite {
		private final Label hostLabel;
		private final Label portLabel;
		private final Text hostText;
		private final Text portText;
		private final Label userLabel;
		private final Text userText;
		private final StyledText debugText;

		DebugFriendsComposite(Composite parent) {
			super(parent, SWT.NULL);
			setLayout(new MigLayout("fill", "[][grow]", "[][][][grow]"));
			hostLabel = Swts.createMigLabel(this, "Host", "");
			hostText = Swts.createMigText(this, "localhost", "growx,wrap");
			portLabel = Swts.createMigLabel(this, "Port", "");
			portText = Swts.createMigText(this, "80", "growx,wrap");
			userLabel = Swts.createMigLabel(this, "User", "");
			userText = Swts.createMigText(this, "me", "growx,wrap");
			debugText = Swts.createMigStyledText(this, SWT.READ_ONLY | SWT.WRAP, "", "grow,spanx");
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		composite = new DebugFriendsComposite(parent);
		final SoftwareFmPlugin plugin = SoftwareFmPlugin.getDefault();
		plugin.getSelectionBindingManager().addSelectedArtifactSelectionListener(new SelectedBindingAdapter() {
			@Override
			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				String uri = "user/friendsUsage/" + composite.userText.getText();
				IResponse response = IHttpClient.Utils.builder().host(composite.hostText.getText(), Integer.parseInt(composite.portText.getText())).//
						get(uri).execute();
				composite.debugText.setText("GET " + uri + " " + response.statusCode() +"\n" + response);
				HostOffsetAndUrl classAndMethodUrl = plugin.getContainer().urlStrategy.classAndMethodUrl(codeData);
			}

			@Override
			public boolean invalid() {
				return composite.isDisposed();
			}
		});
	}

	@Override
	public void setFocus() {
	}

}