package org.softwareFm.displayTweets;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.ICodec;
import org.softwareFm.displayCore.api.ILineEditable;
import org.softwareFm.displayLists.AbstractLineEditor;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.utilities.collections.ICrud;

public class TweetLineEditor extends AbstractLineEditor<String, TweetLinePanel> {

	public TweetLineEditor() {
		super(ICodec.Utils.identityEncoder());
	}

	@Override
	protected void addButtons(ILineEditable<String> lineEditable, Composite parent, int index, final TweetLinePanel buttonParent) {
		ImageButtons.addBrowseButton(buttonParent, GeneralAnchor.browseKey, new Callable<String>() {
			@Override
			public String call() throws Exception {
				return "http://mobile.twitter.com/" + buttonParent.getText();
			}
		});
		super.addButtons(lineEditable, parent, index, buttonParent);
	}

	@Override
	public IHasControl makeLineControl(final ILineEditable<String> lineEditable, Composite parent, final int index, String t) {
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
		if (result != null && result.trim().length() > 0) {
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
			if (result.trim().length() > 0)
				model.set(index, result);
			else
				model.delete(index);
			lineEditable.sendDataToServer();
		}
	}
}
