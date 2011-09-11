package org.softwareFm.debugPlugin.events;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.debugPlugin.DebugPanelConstants;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.TitleAndStyledTextField;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.events.IListenerListListener;
import org.softwareFm.utilities.events.ListenerList;
import org.softwareFm.utilities.strings.Strings;

public class DebugEventsPanel extends Composite implements IListenerListListener {
	private final int logSize;
	private final List<String> log = Collections.synchronizedList(Lists.<String> newList());
	private final TitleAndStyledTextField titleAndStyledTextField;

	public DebugEventsPanel(Composite parent, int style, ConfigForTitleAnd config, int logSize) {
		super(parent, style);
		this.logSize = logSize;
		setLayout(new FormLayout());

		titleAndStyledTextField = new TitleAndStyledTextField(config, this, DebugPanelConstants.eventsKey);
		FormData fd_titleAndStyledTextField = new FormData();
		fd_titleAndStyledTextField.bottom = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.right = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.top = new FormAttachment(0, 0);
		fd_titleAndStyledTextField.left = new FormAttachment(0, 0);
		titleAndStyledTextField.getControl().setLayoutData(fd_titleAndStyledTextField);
		ListenerList.addListenerListListener(this);
	}

	@Override
	public <L, T> void eventOccured(final List<L> Listener, final ICallback<T> callback) {
		if (!isDisposed())
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
