package org.softwarefm.core.composite;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingListener;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.runnable.Callables;

public class DebugTextComposite extends SoftwareFmComposite {

	private TabFolder tabFolder;
	private StyledText listenersText;
	private StyledText viewsText;
	private Callable<Map<Object, List<Object>>> viewGetter;
	private StyledText messageText;

	public DebugTextComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container);
		TabItem listenerItem = new TabItem(tabFolder, SWT.NULL);
		final StyledText listenerText = new StyledText(tabFolder, SWT.WRAP | SWT.READ_ONLY);
		listenerItem.setText("Listener");
		listenerItem.setControl(listenerText);

		TabItem listenersItem = new TabItem(tabFolder, SWT.NULL);
		listenersItem.setText("Listeners");
		listenersText = new StyledText(tabFolder, SWT.READ_ONLY);
		listenersItem.setControl(listenersText);
		FocusListener listener = new FocusListener() {
			public void focusLost(FocusEvent e) {
				updateListenersAndViews();
			}

			public void focusGained(FocusEvent e) {
				updateListenersAndViews();
			}
		};

		TabItem viewsItem = new TabItem(tabFolder, SWT.NULL);
		viewsText = new StyledText(tabFolder, SWT.READ_ONLY);
		viewsItem.setText("Views");
		viewsItem.setControl(viewsText);

		TabItem messageItem = new TabItem(tabFolder, SWT.NULL);
		messageText = new StyledText(tabFolder, SWT.V_SCROLL);
		messageItem.setText("Messages");
		messageItem.setControl(messageText);

		listenersText.addFocusListener(listener);
		viewsText.addFocusListener(listener);

		addSelectedBindingListener(new ISelectedBindingListener() {
			public void notJavaElement(int selectionCount) {
				listenerText.append("notJavaElement: (" + selectionCount + ")\n");
			}

			public void artifactDetermined(int selectionCount, ArtifactData artifactData) {
				listenerText.append("projectDetermined: (" + selectionCount + ")\n" + artifactData + "\nUrl: " + container.urlStrategy.projectUrl(artifactData) + "\n");
			}

			public void notInAJar(int selectionCount, File file) {
				listenerText.append("Not In A Jar: (" + selectionCount + ") file is: " + file + "\n");
			}

			public void digestDetermined(int selectionCount, FileAndDigest fileAndDigest) {
				listenerText.append("Digest: (" + selectionCount + ") " + fileAndDigest + "\nUrl: " + container.urlStrategy.digestUrl(fileAndDigest.digest) + "\n");
			}

			public void codeSelectionOccured(int selectionCount, CodeData codeData) {
				updateListenersAndViews();
				listenerText.setText("Class and method: (" + selectionCount + ")\n" + codeData + "\nUrl: " + container.urlStrategy.classAndMethodUrl(codeData) + "\n");
			}

			public void unknownDigest(int selectionCount, FileAndDigest fileAndDigest) {
				listenerText.append("Unknown Digest: (" + selectionCount + ") " + fileAndDigest + "\n");
			}

			public boolean isValid() {
				return !getComposite().isDisposed();
			}

			@Override
			public String toString() {
				return DebugTextComposite.this.getClass().getSimpleName();
			}

			@Override
			public void friendsArtifactUsage(ArtifactData artifactData, Map<FriendData, UsageStatData> friendsUsage) {
				listenerText.append("friendsArtifactUsage: (" + artifactData + "," + friendsUsage + ")\n");
			}

			@Override
			public void friendsCodeUsage(CodeData codeData, Map<FriendData, UsageStatData> friendsUsage) {
				listenerText.append("friendsArtifactUsage: (" + codeData + "," + friendsUsage + ")\n");
			}
		});
	}

	public ICallback2<Object, String> logger() {
		return new ICallback2<Object, String>() {
			public void process(Object first, String second) throws Exception {
				if (!messageText.isDisposed())
					messageText.append(Thread.currentThread() + " " + first.getClass().getSimpleName() + " - " + second + "\n");
			}
		};

	}

	public void setViewGetter(Callable<Map<Object, List<Object>>> viewGetter) {
		this.viewGetter = viewGetter;
	}

	protected void updateListenersAndViews() {
		ISelectedBindingManager<?> selectedBindingManager = container.selectedBindingManager;
		String join = selectedBindingManager.getListeners().toString();
		listenersText.setText(join);
		if (viewGetter == null)
			viewsText.setText("Not set up");
		else {
			Map<Object, List<Object>> views = Callables.call(viewGetter);
			StringBuilder builder = new StringBuilder();
			for (Entry<Object, List<Object>> viewAndListEntry : views.entrySet()) {
				Object view = viewAndListEntry.getKey();
				add(builder, "", view);
				for (Object control : viewAndListEntry.getValue()) {
					add(builder, "  ", control);
				}
			}
			viewsText.setText(builder.toString());
		}
	}

	private void add(StringBuilder builder, String string, Object object) {
		builder.append(string + object.getClass().getSimpleName() + "@" + System.identityHashCode(object) + ":" + object.toString() + "\n");

	}

	@Override
	protected Composite makeComposite(Composite parent, ImageRegistry imageRegistry) {
		return tabFolder = new TabFolder(parent, SWT.NULL);
	}

}
