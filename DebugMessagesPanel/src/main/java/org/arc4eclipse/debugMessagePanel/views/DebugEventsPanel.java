package org.arc4eclipse.debugMessagePanel.views;

import java.util.Collections;
import java.util.List;

import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.text.TitleAndStyledTextField;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.events.IListenerListListener;
import org.arc4eclipse.utilities.events.ListenerList;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class DebugEventsPanel extends Composite implements IListenerListListener {
	private final int logSize;
	private final List<String> log = Collections.synchronizedList(Lists.<String> newList());
	private final TitleAndStyledTextField titleAndStyledTextField;

	public DebugEventsPanel(Composite parent, int style, Images images, int logSize) {
		super(parent, style);
		this.logSize = logSize;
		setLayout(new FormLayout());

		titleAndStyledTextField = new TitleAndStyledTextField(this, SWT.NONE, images, "Events");
		FormData fd_titleAndStyledTextField = new FormData();
		fd_titleAndStyledTextField.bottom = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.right = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.top = new FormAttachment(0, 0);
		fd_titleAndStyledTextField.left = new FormAttachment(0, 0);
		titleAndStyledTextField.setLayoutData(fd_titleAndStyledTextField);
		ListenerList.addListenerListListener(this);
	}

	@Override
	public <L, T> void eventOccured(final List<L> Listener, final ICallback<T> callback) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final StringBuilder builder = new StringBuilder();
				builder.append(callback);
				builder.append(" -----");
				builder.append(Thread.currentThread().getName());
				builder.append(" ----->");
				builder.append(" \n");
				for (L l : Listener)
					builder.append("   " + l + "\n");
				titleAndStyledTextField.setText(Strings.addToRollingLog(log, logSize, "\n", builder.toString()));
			}
		});
	}
}
