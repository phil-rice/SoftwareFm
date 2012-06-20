package org.softwarefm.eclipse.composite;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.runnable.Callables;
import org.softwarefm.utilities.strings.Strings;

public class DebugTextComposite extends SoftwareFmComposite {

	private TabFolder tabFolder;
	private final SoftwareFmContainer<?> container;
	private StyledText listenersText;
	private StyledText viewsText;
	private Callable<Map<Object, List<Object>>> viewGetter;
	private StyledText messageText;

	public DebugTextComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container);
		this.container = container;
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

		addListener(new ISelectedBindingListener() {
			public void notJavaElement(int selectionCount) {
				listenerText.append("notJavaElement: (" + selectionCount + ")\n");
			}

			public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
				listenerText.append("projectDetermined: (" + selectionCount + ")\n" + artifactData + "\nUrl: " + container.urlStrategy.projectUrl(artifactData) + "\n");
			}

			public void notInAJar(File file, int selectionCount) {
				listenerText.append("Not In A Jar: (" + selectionCount + ") file is: " + file + "\n");
			}

			public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
				listenerText.append("Digest: (" + selectionCount + ") " + fileAndDigest + "\nUrl: " + container.urlStrategy.digestUrl(fileAndDigest.digest) + "\n");
			}

			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				updateListenersAndViews();
				listenerText.setText("Class and method: (" + selectionCount + ")\n" + codeData + "\nUrl: " + container.urlStrategy.classAndMethodUrl(codeData) + "\n");
			}

			public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
				listenerText.append("Unknown Digest: (" + selectionCount + ") " + fileAndDigest + "\n");
			}

			public boolean invalid() {
				return getComposite().isDisposed();
			}

			@Override
			public String toString() {
				return DebugTextComposite.this.getClass().getSimpleName();
			}
		});
	}

	public ICallback2<Object, String> logger() {
		return new ICallback2<Object, String>() {
			public void process(Object first, String second) throws Exception {
				messageText.append(Thread.currentThread() + " " + first.getClass().getSimpleName() + " - " + second+"\n");
			}
		};

	}

	public void setViewGetter(Callable<Map<Object, List<Object>>> viewGetter) {
		this.viewGetter = viewGetter;
	}

	protected void updateListenersAndViews() {
		ISelectedBindingManager<?> selectedBindingManager = container.selectedBindingManager;
		String join = Strings.join(selectedBindingManager.listeners(), "\n");
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
	protected Composite makeComposite(Composite parent) {
		return tabFolder = new TabFolder(parent, SWT.NULL);
	}

}
