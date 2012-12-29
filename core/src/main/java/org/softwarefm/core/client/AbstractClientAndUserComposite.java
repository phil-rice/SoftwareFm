package org.softwarefm.core.client;

import java.util.concurrent.atomic.AtomicReference;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.http.IHttpClient;

abstract public class AbstractClientAndUserComposite extends AbstractMigComposite implements IUserConnectionDetails {

	private final Text hostText;
	private final Text portText;
	private final Text userText;

	public AbstractClientAndUserComposite(Composite parent, MigLayout layout) {
		super(parent, layout);
		final Composite composite = getComposite();
		hostText = Swts.createMigLabelAndTextForForm(composite, "Host", "localhost");
		portText = Swts.createMigLabelAndTextForForm(composite, "Port", "8082");
		userText = Swts.createMigLabelAndTextForForm(composite, "User", "me");
	}

	public String getHost() {
		return getText(hostText);
	}

	public int getPort() {
		return Integer.parseInt(getText(portText));
	}

	public String getUser() {
		return getText(userText);
	}

	protected String getText(final Text text) {
		final AtomicReference<String> result = new AtomicReference<String>();
		Display display = getComposite().getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				result.set(text.getText());
			}
		});
		return result.get();

	}

	public IHttpClient getHttpClient() {
		return IHttpClient.Utils.builder().host(getHost(), getPort());
	}

}
