package org.arc4eclipse.displayTweets;

import java.util.concurrent.Callable;

import org.arc4eclipse.displayCore.api.ICodec;
import org.arc4eclipse.displayCore.api.ILineEditable;
import org.arc4eclipse.displayLists.AbstractLineEditor;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.utilities.collections.ICrud;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TweetLineEditor extends AbstractLineEditor<String, TweetLinePanel> {

	public TweetLineEditor() {
		super(ICodec.Utils.identityEncoder());
	}

	@Override
	protected void addButtons(ILineEditable<String> lineEditable, Composite parent, int index, final TweetLinePanel buttonParent) {
		ImageButtons.addBrowseButton(buttonParent, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return "http://mobile.twitter.com/" + buttonParent.getText();
			}
		});
		super.addButtons(lineEditable, parent, index, buttonParent);
	}

	@Override
	public Control makeLineControl(final ILineEditable<String> lineEditable, Composite parent, final int index, String t) {
		TweetLinePanel text = new TweetLinePanel(parent, SWT.NULL, lineEditable.getDisplayerContext());
		text.setText(t);
		addButtons(lineEditable, parent, index, text);
		return text;
	}

	@Override
	public void add(ILineEditable<String> lineEditable) {
		ConfigForTitleAnd forDialogs = lineEditable.getDialogConfig();
		TweetDialog dialog = new TweetDialog(lineEditable.getShell(), SWT.NULL, forDialogs, DisplayTweetListConstants.tweetLineTitleKey);
		String result = dialog.open("");
		if (result != null) {
			lineEditable.getModel().add(result);
			lineEditable.sendDataToServer();

		}
	}

	@Override
	public void edit(ILineEditable<String> lineEditable, int index) {
		ConfigForTitleAnd forDialogs = lineEditable.getDialogConfig();
		TweetDialog dialog = new TweetDialog(lineEditable.getShell(), SWT.NULL, forDialogs, DisplayTweetListConstants.tweetLineTitleKey);
		ICrud<String> model = lineEditable.getModel();
		String result = dialog.open(model.get(index));
		if (result != null) {
			model.set(index, result);
			lineEditable.sendDataToServer();

		}
	}

}
