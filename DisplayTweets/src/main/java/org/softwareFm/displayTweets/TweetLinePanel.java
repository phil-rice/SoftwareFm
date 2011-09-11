package org.softwareFm.displayTweets;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class TweetLinePanel implements IHasControl, IButtonParent {

	private final ImageRegistry imageRegistry;
	private final IResourceGetter resourceGetter;
	private final Text text;
	private final Composite content;

	public TweetLinePanel(Composite parent, int style, DisplayerContext displayerContext) {
		this.content = new Composite(parent, style);
		content.setLayout(new RowLayout());
		this.imageRegistry = displayerContext.imageRegistry;
		this.resourceGetter = displayerContext.resourceGetter;
		text = new Text(content, displayerContext.configForTitleAnd.style);
		text.setEditable(false);
		text.setBackground(displayerContext.configForTitleAnd.normalBackground);
		ConfigForTitleAnd config = displayerContext.configForTitleAnd;
		text.setLayoutData(new RowData(config.titleWidth, config.titleHeight));
	}

	@Override
	public Composite getButtonComposite() {
		return content;
	}

	@Override
	public ImageRegistry getImageRegistry() {
		return imageRegistry;
	}

	@Override
	public IResourceGetter getResourceGetter() {
		return resourceGetter;
	}

	@Override
	public void buttonAdded() {
	}

	public void setText(String newText) {
		text.setText(Strings.nullSafeToString(newText));
	}

	public String getText() {
		return text.getText();
	}

	@Override
	public Control getControl() {
		return content;
	}

}
